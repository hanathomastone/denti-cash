package com.kaii.dentix.domain.blockChain.token.dto;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenLedgerDto {

    private Long id;
    private String senderAddress;
    private String receiverAddress;
    private String senderLoginIdentifier;
    private String receiverLoginIdentifier;
    private Long amount;
    private String type;
    private String status;
    private String message;
    private Date created;
    private String sourceType;

    public static TokenLedgerDto from(TokenLedger ledger) {
        return TokenLedgerDto.builder()
                .id(ledger.getId())
                .senderAddress(
                        ledger.getSenderUserWallet() != null ? ledger.getSenderUserWallet().getAddress()
                                : ledger.getSenderAdminWallet() != null ? ledger.getSenderAdminWallet().getAddress()
                                : null
                )
                .receiverAddress(
                        ledger.getReceiverUserWallet() != null ? ledger.getReceiverUserWallet().getAddress()
                                : ledger.getReceiverAdminWallet() != null ? ledger.getReceiverAdminWallet().getAddress()
                                : null
                )
                .senderLoginIdentifier(
                        ledger.getSenderUserWallet() != null && ledger.getSenderUserWallet().getUser() != null
                                ? ledger.getSenderUserWallet().getUser().getUserLoginIdentifier()
                                : null
                )
                .receiverLoginIdentifier(
                        ledger.getReceiverUserWallet() != null && ledger.getReceiverUserWallet().getUser() != null
                                ? ledger.getReceiverUserWallet().getUser().getUserLoginIdentifier()
                                : null
                )
                .amount(ledger.getAmount())
                .type(ledger.getType() != null ? ledger.getType().name() : null)
                .status(ledger.getStatus() != null ? ledger.getStatus().name() : null)
                .message(ledger.getMessage())
                .created(ledger.getCreated())

                //TokenLedgerSourceType 출력
                .sourceType(ledger.getSourceType() != null ? ledger.getSourceType().name() : null)

                .build();
    }
}
