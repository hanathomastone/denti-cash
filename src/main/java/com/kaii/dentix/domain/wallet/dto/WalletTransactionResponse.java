package com.kaii.dentix.domain.wallet.dto;

import com.kaii.dentix.domain.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransactionResponse {
    private Long transactionId;
    private String walletAddress;
    private String userName;
    private TransactionType transactionType;
    private Long amount;
    private String description;
//    private LocalDateTime created;
}