package com.kaii.dentix.domain.questionnaire.controller;

import com.kaii.dentix.domain.questionnaire.application.QuestionnaireService;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireTemplateDtoList;
import com.kaii.dentix.global.common.response.DataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @GetMapping(value = "/template", name = "문진표 양식 조회")
    public DataResponse<QuestionnaireTemplateDtoList> questionnaireTemplate() throws IOException {
        return new DataResponse<>(new QuestionnaireTemplateDtoList(questionnaireService.getQuestionnaireTemplate()));
    }
}
