package com.kaii.dentix.domain.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class OralStatusTypeDto {

    private String type;
    private String title;
}
