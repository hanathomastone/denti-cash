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
public class AWSS3Service {

    private final AmazonS3 s3;
    private final String bucketName;

    public AWSS3Service(
            @Value("${s3.accessKey}") String accessKey,
            @Value("${s3.secretKey}") String secretKey,
            @Value("${s3.storage.regionName}") String regionName,
            @Value("${s3.storage.bucketName}") String bucketName
    ) {
        this.bucketName = bucketName;
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.s3 = AmazonS3ClientBuilder.standard()
                .withRegion(regionName) // ex) ap-northeast-2
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    /**
     * 파일 업로드
     */
    public String upload(MultipartFile file, String path, boolean isTime) throws IOException {
        String originFileName = file.getOriginalFilename();
        String originFileNameNotExt = originFileName.substring(0, originFileName.lastIndexOf("."));
        String fileExt = originFileName.substring(originFileName.lastIndexOf(".") + 1);
        String fileName = isTime
                ? originFileNameNotExt + "_" + System.currentTimeMillis() + "." + fileExt
                : originFileName;
        String filePath = path + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        s3.putObject(new PutObjectRequest(bucketName, filePath, file.getInputStream(), metadata));
//                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3.getUrl(bucketName, filePath).toString();
    }

    /**
     * 파일 삭제
     */
    public void delete(String key) {
        s3.deleteObject(bucketName, key);
    }

    /**
     * 파일 다운로드
     */
    public S3Object getFile(String key) {
        return s3.getObject(new GetObjectRequest(bucketName, key));
    }
}
