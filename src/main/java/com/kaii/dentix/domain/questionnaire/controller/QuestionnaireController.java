package com.kaii.dentix.domain.questionnaire.controller;

import com.kaii.dentix.domain.questionnaire.application.QuestionnaireService;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireIdDto;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireResultDto;
import com.kaii.dentix.domain.questionnaire.dto.QuestionnaireTemplateJsonDto;
import com.kaii.dentix.domain.questionnaire.dto.request.QuestionnaireSubmitRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/questionnaire")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @GetMapping(value = "/template", name = "문진표 양식 조회")
    public DataResponse<QuestionnaireTemplateJsonDto> questionnaireTemplate() throws IOException {
        return new DataResponse<>(questionnaireService.getQuestionnaireTemplate());
    }

    @PostMapping(value = "/submit", name = "문진표 제출")
    public DataResponse<QuestionnaireIdDto> questionnaireSubmit(HttpServletRequest httpServletRequest, @Valid @RequestBody QuestionnaireSubmitRequest request) throws IOException {
        return new DataResponse<>(questionnaireService.questionnaireSubmit(httpServletRequest, request));
    }

    @GetMapping(value = "/result", name = "문진표 결과 조회")
    public DataResponse<QuestionnaireResultDto> questionnaireResult(HttpServletRequest httpServletRequest, @RequestParam @Min(1) long questionnaireId) {
        return new DataResponse<>(questionnaireService.questionnaireResult(httpServletRequest, questionnaireId));
    }
}
