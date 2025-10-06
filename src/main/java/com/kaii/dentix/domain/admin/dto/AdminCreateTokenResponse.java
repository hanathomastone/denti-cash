package com.kaii.dentix.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateTokenResponse {
    private String contractAddress;
    private Long newBalance;
}