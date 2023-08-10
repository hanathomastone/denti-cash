package com.kaii.dentix.domain.findPwdQuestion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class UserFindPwdQuestionsDto {

    private Long id;

    private Long sort;

    private String title;

}
