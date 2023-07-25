package com.kaii.dentix.domain.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionnaireTemplateJsonDto {

    private String version;
    private List<QuestionnaireTemplateDto> template;
}
