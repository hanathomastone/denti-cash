package com.kaii.dentix.domain.blockChain.token.dto;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenLedgerSummaryDto {
    private Long id;
    private String type;       // REWARD, USE, TRANSFER 등
    private Long amount;
    private String status;     // SUCCESS / FAILED
    private String message;
    private LocalDateTime created;

    public static TokenLedgerSummaryDto fromEntity(TokenLedger ledger) {
        return TokenLedgerSummaryDto.builder()
                .id(ledger.getId())
                .type(ledger.getType().name())
                .amount(ledger.getAmount())
                .status(ledger.getStatus().name())
                .message(ledger.getMessage())
                .created(LocalDateTime.now())
                .build();
    }
}