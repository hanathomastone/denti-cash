package com.kaii.dentix.global.common.util;

import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class LambdaService {

    @Value("${lambda.pyDentalPat.apiUrl}")
    private String pyDentalApiUrl;
    @Value("${lambda.pyDentalPat.apiKey}")
    private String pyDentalApiKey;

    @Value("${s3.storage.bucketName}")
    private String bucket;

    private final RestTemplate restTemplate;

    public LambdaService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public OralCheckAnalysisResponse getPyDentalLambda(String imagePath) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("x-api-key", pyDentalApiKey);

        Map<String, Object> params = new HashMap<>();
        params.put("bucket", "kaii-denti-roka"); // TODO : bucketName 수정 필요
        params.put("imagePath", imagePath);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(pyDentalApiUrl, entity, OralCheckAnalysisResponse.class);
    }

}
