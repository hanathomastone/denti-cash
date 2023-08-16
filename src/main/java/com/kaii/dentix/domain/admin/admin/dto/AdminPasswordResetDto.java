package com.kaii.dentix.domain.admin.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor @NoArgsConstructor
public class AdminPasswordResetDto {

    private String adminPassword;

}
