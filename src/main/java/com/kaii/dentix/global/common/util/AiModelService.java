package com.kaii.dentix.global.common.util;

import com.kaii.dentix.domain.oralCheck.dto.resoponse.OralCheckAnalysisResponse;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AiModelService {

    @Value("${aiModel.apiUrl}")
    private String aiModelApiUrl;

    private final RestTemplate restTemplate;

    public AiModelService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @SneakyThrows
    @Async
    public OralCheckAnalysisResponse getPyDentalAiModel(MultipartFile picture) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        ByteArrayResource fileResource = new ByteArrayResource(picture.getBytes()) {
            @Override
            public String getFilename() {
                return picture.getOriginalFilename();
            }
        };

        params.add("picture", fileResource);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(aiModelApiUrl, entity, OralCheckAnalysisResponse.class);
    }

}
