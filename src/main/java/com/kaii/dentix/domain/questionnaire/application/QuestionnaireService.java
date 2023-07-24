package com.kaii.dentix.domain.questionnaire.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireTemplateDto;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final ObjectMapper objectMapper;

    /**
     * 문진표 양식 조회
     */
    public List<QuestionnaireTemplateDto> getQuestionnaireTemplate() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("template/questionnaire.json");
        if (!classPathResource.exists()) throw new BadRequestApiException("파일이 존재하지 않습니다.");

        // 서버 환경 대응
        InputStream inputStream = classPathResource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);

        // spring 3버전 버그로 인해 List.class를 사용하면 가져다 쓸 때 java.util.LinkedHashMap cannot be cast to object 오류가 발생하여 TypeReference로 선언해야 함
        return objectMapper.readValue(new String(bytes), new TypeReference<>(){});
    }
}
