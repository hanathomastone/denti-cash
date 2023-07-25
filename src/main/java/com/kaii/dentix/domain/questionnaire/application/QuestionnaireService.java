package com.kaii.dentix.domain.questionnaire.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaii.dentix.domain.oralStatus.domain.OralStatus;
import com.kaii.dentix.domain.questionnaire.dao.QuestionnaireRepository;
import com.kaii.dentix.domain.questionnaire.domain.Questionnaire;
import com.kaii.dentix.domain.questionnaire.dto.*;
import com.kaii.dentix.domain.questionnaire.dto.request.QuestionnaireSubmitRequest;
import com.kaii.dentix.domain.user.application.UserService;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.error.exception.BadRequestApiException;
import com.kaii.dentix.global.common.error.exception.FormValidationException;
import com.kaii.dentix.global.common.error.exception.NotFoundDataException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
public class QuestionnaireService {

    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final QuestionnaireRepository questionnaireRepository;

    /**
     * 문진표 양식 조회
     */
    public QuestionnaireTemplateJsonDto getQuestionnaireTemplate() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("template/questionnaire.json");
        if (!classPathResource.exists()) throw new BadRequestApiException("파일이 존재하지 않습니다.");

        // 서버 환경 대응
        InputStream inputStream = classPathResource.getInputStream();
        byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);

        // spring 3버전 버그로 인해 List.class를 사용하면 가져다 쓸 때 java.util.LinkedHashMap cannot be cast to object 오류가 발생하여 TypeReference로 선언해야 함
        return objectMapper.readValue(new String(bytes), new TypeReference<>(){});
    }

    /**
     * 문진표 제출
     */
    @Transactional(rollbackFor = Exception.class)
    public QuestionnaireIdDto questionnaireSubmit(HttpServletRequest httpServletRequest, QuestionnaireSubmitRequest request) throws IOException {
        User user = userService.getTokenUser(httpServletRequest);

        this.questionnaireValidate(request.getForm());

        // TODO : AI 연동 및 상태값 도출
        Random random = new Random();
        int typeCount = random.nextInt(2) + 1;
        String[] chars = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K"};
        List<String> typeList = new ArrayList<>();
        for (int i = 0; i < typeCount; i++) {
            int randomIndex = random.nextInt(chars.length);
            if (typeList.stream().anyMatch(type -> type.equals(chars[randomIndex]))) {
                i--; continue;
            }
            typeList.add(chars[randomIndex]);
        }

        Questionnaire questionnaire = questionnaireRepository.save(new Questionnaire(user.getUserId(), objectMapper.writeValueAsString(request.getForm()), typeList));

        return new QuestionnaireIdDto(questionnaire.getQuestionnaireId());
    }

    /**
     * 문진표 결과 조회
     */
    @Transactional(readOnly = true)
    public QuestionnaireResultDto questionnaireResult(long questionnaireId) {
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId).orElseThrow(() -> new NotFoundDataException("문진표가 존재하지 않습니다."));

        List<OralStatusTypeInfoDto> oralStatusList = questionnaire.getUserOralStatusList().stream()
            .map(userOralStatus -> {
                OralStatus oralStatus = userOralStatus.getOralStatus();
                return OralStatusTypeInfoDto.builder()
                    .type(oralStatus.getOralStatusType())
                    .title(oralStatus.getOralStatusTitle())
                    .description(oralStatus.getOralStatusDescription())
                    .build();
            }).toList();

        return new QuestionnaireResultDto(questionnaire.getCreated(), oralStatusList);
    }

    /**
     * 문진표 양식 기준으로 validation 진행
     */
    private void questionnaireValidate(QuestionnaireFormDto formDto) throws IOException {
        HashMap<String, Object> form = objectMapper.convertValue(formDto, HashMap.class);

        List<QuestionnaireTemplateDto> questionnaireTemplate = this.getQuestionnaireTemplate().getTemplate();
        questionnaireTemplate.forEach(template -> {
            Object valueObj = form.getOrDefault(template.getKey(), null);
            // 값 존재 확인
            if (valueObj == null) {
                throw new FormValidationException(String.format("%s번 문항을 입력해 주세요.", template.getNumber()));
            }

            int[] normalValues = template.getContents().stream().mapToInt(QuestionnaireTemplateContentDto::getId).toArray();
            if (template.isMultiple()) {
                // 중복 선택의 경우
                Integer[] values = objectMapper.convertValue(valueObj, Integer[].class);
                if (values.length == 0) {
                    throw new FormValidationException(String.format("%s번 문항을 입력해 주세요.", template.getNumber()));
                }

                List<Integer> alreadyValues = new ArrayList<>();
                Arrays.stream(values).forEach(value -> {
                    // 유효하지 않은 값 존재 확인
                    if (Arrays.stream(normalValues).noneMatch(nv -> nv == value)) {
                        throw new FormValidationException(String.format("%s번 문항에 %d 값은 유효하지 않습니다.", template.getNumber(), value));
                    }
                    // 중복 값 존재 확인
                    if (alreadyValues.stream().anyMatch(nv -> nv.equals(value))) {
                        throw new FormValidationException(String.format("%s번 문항에 %d 값이 중복으로 존재합니다.", template.getNumber(), value));
                    }
                    alreadyValues.add(value);
                });
            } else {
                // 중복 선택이 아닌 경우
                int value = (int) valueObj;
                // 유효하지 않은 값 존재 확인
                if (Arrays.stream(normalValues).noneMatch(nv -> nv == value)) {
                    throw new FormValidationException(String.format("%s번 문항에 %d 값은 유효하지 않습니다.", template.getNumber(), value));
                }
            }
        });
    }
}
