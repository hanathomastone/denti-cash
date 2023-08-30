package com.kaii.dentix.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminUserSignUpCountDto {

    private Long countAll; // 전체 가입자

    private Long countMan; // 남성 가입자

    private Long countWoman; // 여성 가입자

}
