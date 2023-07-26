package com.kaii.dentix.domain.questionnaire.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class QuestionnaireTemplateDto {

    private int sort;
    private String key;
    private String number;
    private String title;
    private String description;
    private Integer minimum;
    private Integer maximum;
    private List<QuestionnaireTemplateContentDto> contents;
}
