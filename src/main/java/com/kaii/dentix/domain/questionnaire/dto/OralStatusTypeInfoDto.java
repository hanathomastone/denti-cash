package com.kaii.dentix.domain.questionnaire.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class OralStatusTypeInfoDto {

    private String type;
    private String title;
    private String description;
}
