package com.kaii.dentix.domain.wallet.dto;

import com.kaii.dentix.domain.wallet.domain.Wallet;

import java.util.List;

public record WalletDto(
        Long id,
        Long balance,
        List<WalletTransactionDto> transactions
) {
    public static WalletDto from(Wallet wallet) {
        return new WalletDto(
                wallet.getId(),
                wallet.getBalance(),
                wallet.getTransactions()
                        .stream()
                        .map(WalletTransactionDto::from)
                        .toList()
        );
    }
}