//package com.kaii.dentix.domain.blockChain.wallet.dto;
//
//import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
//
//import java.util.List;
//
//public record WalletDto(
//        Long id,
//        Long balance,
//        List<WalletTransactionDto> transactions
//) {
//    public static WalletDto from(UserWallet userWallet) {
//        return new WalletDto(
//                userWallet.getId(),
//                userWallet.getBalance(),
//                userWallet.getTransactions()
//                        .stream()
//                        .map(WalletTransactionDto::from)
//                        .toList()
//        );
//    }
//}