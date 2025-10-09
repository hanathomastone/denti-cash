package com.kaii.dentix.domain.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminWalletSummaryDto {
    private Long adminWalletId;;
    private String address;
    private Long totalIssued; // 총 발급량
    private Long totalUsed;   // 지급량
    private Long balance;     // 잔여량
}