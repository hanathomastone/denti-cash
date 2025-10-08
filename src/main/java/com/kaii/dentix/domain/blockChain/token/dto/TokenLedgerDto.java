package com.kaii.dentix.domain.blockChain.token.dto;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenLedgerDto {

    private Long id;
    private String senderAddress;
    private String receiverAddress;
    private Long amount;
    private String type;
    private String status;
    private String message;
    private LocalDateTime created;

    public static TokenLedgerDto from(TokenLedger ledger) {
        return TokenLedgerDto.builder()
                .id(ledger.getId())
                .amount(ledger.getAmount())
                .type(ledger.getType().name())
                .status(ledger.getStatus().name())
                .message(ledger.getMessage())
                .build();
    }
}
