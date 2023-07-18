package com.kaii.dentix.global.common.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.kaii.dentix.global.common.aws.dto.NcsStsRequestDTO;
import com.kaii.dentix.global.common.aws.handler.RestTemplateResponseErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private AmazonS3 s3;
    private ObjectMetadata objectMetadata;

    private String tempAccessKey;
    private String tempAccessKeyExpireTime;

//    private final ErrorLogJpaRepository errorLogJpaRepository;

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secretKey}")
    private String secretKey;

    @Value("${s3.storage.endPoint}")
    private String endPoint;

    @Value("${s3.storage.regionName}")
    private String regionName;

    @Value("${s3.storage.bucketName}")
    private String bucketName;

    @Setter
    private String fileExt;
    private String filePath = "upload";
    private long fileSize;

    /**
     * S3Client STS 임시 보안 자격 부여
     *
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public AmazonS3 s3Client() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {

        // 서버 timestamp 추출
        String timestamp = Long.toString(currentTimeMillis());

        // signature 생성
        String signature = makeSignature(timestamp, accessKey, secretKey);

        // 유효시간 1시간으로 설정
        NcsStsRequestDTO ncsStsRequestDTO = NcsStsRequestDTO.builder().durationSec(900).build();

        // STS 생성
        String url = "https://sts.apigw.gov-ntruss.com/api/v1/credentials"; // 생성은 POST, 조회는 GET

        RequestEntity<NcsStsRequestDTO> requestEntity = RequestEntity
                .post(url)
                .header("x-ncp-apigw-timestamp", timestamp)
                .header("x-ncp-iam-access-key", accessKey)
                .header("x-ncp-apigw-signature-v2", signature)
                .body(ncsStsRequestDTO);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestTemplateResponseErrorHandler()); // 에러 통과 후 코드 내에서 처리 위한 Handler
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(3000); // connection timeout 3초
        simpleClientHttpRequestFactory.setReadTimeout(5000); // read timeout 5초
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);

        ResponseEntity<Map> response = restTemplate.exchange(requestEntity, Map.class);

        // 200 아닌 경우 에러 핸들링 DB에 로그 남기기
        if (response.getStatusCode() != HttpStatus.OK) {
            Map result = response.getBody();

            if (result != null && result.containsKey("errorCode")) {
                JSONObject json = new JSONObject();
                String errorCode = String.valueOf(result.get("errorCode"));
                String message = String.valueOf(result.get("message"));
                log.error("errorCode : " + errorCode);
                log.error("message : " + message);

                json.put("errorCode", errorCode);
                json.put("message", message);

                if (result.containsKey("actions")) {
                    String actions = String.valueOf(result.get("actions"));
                    log.error("actions : " + actions);

                    json.put("actions", actions);
                }

                // Error Log Save
//                ErrorLog errorLog = ErrorLog.builder()
//                    .errorLogUrl(url)
//                    .errorLogHeader(requestEntity.getHeaders().toString())
//                    .errorLogMethod("POST")
//                    .errorLogMessage(json.toString())
//                    .build();
//
//                errorLogJpaRepository.save(errorLog);

                throw new RuntimeException(errorCode + " : " + message);
            }
        }

        // 200 인 경우
        Map result = response.getBody();
        log.info("result : " + result);

        if (result == null || !result.containsKey("accessKey")) {
            throw new RuntimeException("Not exist accessKey");
        }

        tempAccessKey = result.get("accessKey").toString();
        tempAccessKeyExpireTime = result.get("expireTime").toString();

        if (result.containsKey("accessKey")) {
            log.info("accessKey : " + result.get("accessKey"));
            log.info("secretKey : " + result.get("keySecret"));
            log.info("createTime : " + result.get("createTime"));
            log.info("expireTime : " + result.get("expireTime"));
            log.info("useMfa : " + result.get("useMfa"));

            tempAccessKey = String.valueOf(result.get("accessKey"));

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setConnectionTimeout(3000);
            clientConfiguration.setRequestTimeout(0);

            s3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(result.get("accessKey").toString(), result.get("keySecret").toString())))
                    .withClientConfiguration(clientConfiguration)
                    .build();
        }

        this.objectMetadata = new ObjectMetadata();

        return s3;

    }

    /**
     * 초기화
     *
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public void init(boolean hard) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        if (tempAccessKey == null || hard) {
            s3 = s3Client();

            // ncs 에서 바로 aws로 api 수신이 되지 않아 3초 대기 넣음
            Thread.sleep(3000);
        } else {
            LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
            LocalDateTime expireTime = LocalDateTime.parse(tempAccessKeyExpireTime.replace("T", " ").replace("Z", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.info("now : {}", now);
            log.info("expireTime : {}", expireTime);
            long diff = ChronoUnit.MINUTES.between(now, expireTime);
            log.info("diff : {}", diff);

            if (diff <= 5) {
                s3 = s3Client();

                // ncs 에서 바로 aws로 api 수신이 되지 않아 3초 대기 넣음
                Thread.sleep(3000);
            }
        }
    }

    /**
     * Storage 파일 업로드
     */
    public String upload(MultipartFile file, String path, boolean isTime) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        init(false);

        String originFileName = file.getOriginalFilename(); // 기존 파일명
        String originFileNameNotExt = originFileName.substring(0, originFileName.lastIndexOf(".")); // 확장자 제외한 기존 파일명
        String fileExt = originFileName.substring(originFileName.lastIndexOf(".") + 1); // 업로드 파일 확장자
        String fileName = isTime ? originFileNameNotExt + "_" + currentTimeMillis() + '.' + fileExt : originFileName; // 파일명
        String filePath = path + fileName; // 파일 경로

        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");

        byte[] bytes = file.getBytes(); // MultipartFile to byte[]
        objectMetadata.setContentLength(bytes.length);

        s3.putObject(new PutObjectRequest(bucketName, filePath, file.getInputStream(), objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));

        // 업로드 결과 경로
        return s3.getUrl(bucketName, filePath).toString();
    }

    /**
     * S3 파일 업로드 및 삭제
     * @param file - 업로드 파일 정보
     * @param key - 파일 경로
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    public String uploadAndDelete(byte[] file, String path, String key) throws IOException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        init(false);

        if (key != null) {
            s3.deleteObject(bucketName, key);
        }

        objectMetadata.setContentLength(0L);
        objectMetadata.setContentType("application/x-directory");

        objectMetadata.setContentLength(file.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);

        s3.putObject(new PutObjectRequest(bucketName, path, byteArrayInputStream, objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead));

        // 업로드 결과 경로
        return s3.getUrl(bucketName, path).toString();
    }

    /**
     * Storage 파일 삭제
     */
    public void delete(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        init(false);

        s3.deleteObject(bucketName, key);
    }

    /**
     * CORS 설정
     */
    public void setCors(String bucketName, List<String> domains) {

        BucketCrossOriginConfiguration bucketCrossOriginConfiguration = new BucketCrossOriginConfiguration();
        CORSRule corsRule = new CORSRule();
        corsRule.setAllowedMethods(CORSRule.AllowedMethods.GET, CORSRule.AllowedMethods.PUT, CORSRule.AllowedMethods.POST);
        corsRule.setAllowedOrigins(domains);
        bucketCrossOriginConfiguration.withRules(corsRule);
        s3.setBucketCrossOriginConfiguration(bucketName, bucketCrossOriginConfiguration);

    }

    /**
     * S3 단일 다운로드
     *
     * @param fileName
     * @return S3Object : S3 객체
     */
    public S3Object getS3File(String dirName, String fileName, boolean hard) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, InterruptedException {
        init(hard);

        String filePath = this.filePath + "/" + dirName + "/" + fileName;
        return s3.getObject(new GetObjectRequest(bucketName, filePath));
    }


    /**
     * Ncloud API Signature 만들기
     *
     * @param timestamp - 서버시간
     * @param accessKey - Sub account API AccessKey
     * @param secretKey - Sub account API SecretKey
     * @return String
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public String makeSignature(String timestamp, String accessKey, String secretKey) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/api/v1/credentials";

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        return Base64.encodeBase64String(rawHmac);
    }

}