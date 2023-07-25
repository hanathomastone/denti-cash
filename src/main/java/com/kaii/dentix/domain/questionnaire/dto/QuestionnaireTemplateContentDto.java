package com.kaii.dentix.domain.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class QuestionnaireTemplateContentDto {

    private int sort;
    private int id;
    private String text;
}
