package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 잔액 동기화 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncBalanceResponseDto {
    private Boolean success;
    private String message;
    private Integer syncedCount;
    private LocalDateTime syncedAt;

    public static SyncBalanceResponseDto success(Integer count) {
        return SyncBalanceResponseDto.builder()
                .success(true)
                .message("모든 지갑 잔액이 동기화되었습니다.")
                .syncedCount(count)
                .syncedAt(LocalDateTime.now())
                .build();
    }
}