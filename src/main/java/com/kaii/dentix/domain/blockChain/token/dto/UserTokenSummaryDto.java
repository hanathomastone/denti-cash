package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTokenSummaryDto {
    private Long balance; // ✅ 현재 보유 잔액
    private List<TokenLedgerSummaryDto> earnedList; // ✅ 적립 내역
    private List<TokenLedgerSummaryDto> usedList;   // ✅ 사용 내역
}