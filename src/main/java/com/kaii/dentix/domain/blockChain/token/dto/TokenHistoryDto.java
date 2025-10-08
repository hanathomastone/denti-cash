package com.kaii.dentix.domain.blockChain.token.dto;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenHistoryDto {
    private String type;         // EARN / SPEND / TRANSFER 등
    private Long amount;         // 거래 금액
    private String message;      // 거래 사유
    private Date createdAt;      // 거래 일시

    public static TokenHistoryDto from(TokenLedger ledger) {
        return TokenHistoryDto.builder()
                .type(ledger.getType().name())
                .amount(ledger.getAmount())
                .message(ledger.getMessage())
                .createdAt(ledger.getCreated())
                .build();
    }
}