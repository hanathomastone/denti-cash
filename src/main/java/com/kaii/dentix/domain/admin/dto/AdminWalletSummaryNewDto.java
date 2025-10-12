package com.kaii.dentix.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWalletSummaryNewDto {
    private Long totalBalance;   // 보유 토큰
    private Long totalIssued;    // 지급된 토큰
    private Long totalRemain;    // 잔여 토큰
}