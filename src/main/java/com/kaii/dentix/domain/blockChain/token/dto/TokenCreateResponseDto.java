package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 토큰 생성 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenCreateResponseDto {
    private Boolean success;
    private String contractAddress;
    private String tokenName;
    private String tokenSymbol;
    private Long supply;
    private LocalDateTime createdAt;

    public static TokenCreateResponseDto of(
            String contractAddress,
            String tokenName,
            String tokenSymbol,
            Long supply) {
        return TokenCreateResponseDto.builder()
                .success(true)
                .contractAddress(contractAddress)
                .tokenName(tokenName)
                .tokenSymbol(tokenSymbol)
                .supply(supply)
                .createdAt(LocalDateTime.now())
                .build();
    }
}