package com.kaii.dentix.domain.blockChain.token.dto;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminTokenLedgerDto {
    private Long id;
    private Long amount;
    private TokenLedgerType type;
    private TokenLedgerStatus status;
    private String message;
    private String sender;
    private String receiver;
    private Date created;

    public static AdminTokenLedgerDto from(TokenLedger ledger) {
        return AdminTokenLedgerDto.builder()
                .id(ledger.getId())
                .amount(ledger.getAmount())
                .type(ledger.getType())
                .status(ledger.getStatus())
                .message(ledger.getMessage())
                .sender(
                        ledger.getSenderAdminWallet() != null
                                ? "AdminWallet#" + ledger.getSenderAdminWallet().getAdminWalletId()
                                : (ledger.getSenderUserWallet() != null
                                ? "UserWallet#" + ledger.getSenderUserWallet().getUserWalletId()
                                : "-")
                )
                .receiver(
                        ledger.getReceiverAdminWallet() != null
                                ? "AdminWallet#" + ledger.getReceiverAdminWallet().getAdminWalletId()
                                : (ledger.getReceiverUserWallet() != null
                                ? "UserWallet#" + ledger.getReceiverUserWallet().getUserWalletId()
                                : "-")
                )
                .created(ledger.getCreated())
                .build();
    }
}
