package com.kaii.dentix.domain.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class QuestionnaireAndStatusDto {

    private Long questionnaireId;
    private Date questionnaireCreated;
    private String oralStatusType;
    private String oralStatusTitle;
}
