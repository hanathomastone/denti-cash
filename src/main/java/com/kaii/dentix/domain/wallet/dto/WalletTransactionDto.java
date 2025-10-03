package com.kaii.dentix.domain.wallet.dto;

import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.domain.wallet.domain.WalletTransaction;

public record WalletTransactionDto(
        Long id,
        Long amount,
        TransactionType transactionType,
        String description
) {
    public static WalletTransactionDto from(WalletTransaction tx) {
        return new WalletTransactionDto(
                tx.getId(),
                tx.getAmount(),
                tx.getTransactionType(),
                tx.getDescription()
        );
    }
}