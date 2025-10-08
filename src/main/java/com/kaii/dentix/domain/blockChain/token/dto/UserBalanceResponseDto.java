package com.kaii.dentix.domain.blockChain.token.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;  // ← 이거 확인
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceResponseDto {
    private Long userId;
    private Long balance;
    private String address;
    private LocalDateTime queriedAt;

    public static UserBalanceResponseDto of(Long userId, Long balance, String address) {
        return UserBalanceResponseDto.builder()
                .userId(userId)
                .balance(balance)
                .address(address)
                .queriedAt(LocalDateTime.now())
                .build();
    }
}