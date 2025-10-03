package com.kaii.dentix.domain.wallet.controller;


import com.kaii.dentix.domain.type.TransactionPeriod;
import com.kaii.dentix.domain.type.TransactionSort;
import com.kaii.dentix.domain.type.TransactionTypeOption;
import com.kaii.dentix.domain.type.oral.OralCheckResultType;
import com.kaii.dentix.domain.wallet.domain.WalletTransaction;
import com.kaii.dentix.domain.wallet.dto.WalletDto;
import com.kaii.dentix.domain.wallet.dto.WalletTransactionDto;
import jdk.jfr.Registered;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.kaii.dentix.domain.wallet.application.WalletService;
import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{userId}/charge")
    public ResponseEntity<WalletTransactionDto> charge(
            @PathVariable Long userId,
            @RequestParam Long amount,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(
                WalletTransactionDto.from(walletService.chargeWallet(userId, amount, description))
        );
    }

    @PostMapping("/{userId}/use")
    public ResponseEntity<WalletTransactionDto> use(
            @PathVariable Long userId,
            @RequestParam Long amount,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(
                WalletTransactionDto.from(walletService.useWallet(userId, amount, description))
        );
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<Page<WalletTransactionDto>> getTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "ALL") TransactionTypeOption transactionType,
            @RequestParam(defaultValue = "ALL") TransactionPeriod period,
            @RequestParam(defaultValue = "LATEST") TransactionSort sort,
            Pageable pageable // ✅ page, size, sort 자동 매핑
    ) {
        Page<WalletTransactionDto> result = walletService.getTransactions(userId, transactionType, period, sort, pageable)
                .map(WalletTransactionDto::from);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<WalletDto> getWallet(@PathVariable Long userId) {
        return ResponseEntity.ok(
                WalletDto.from(walletService.getWalletByUserId(userId))
        );
    }

    @PostMapping("/{userId}/rewards/checkup")
    public ResponseEntity<WalletTransactionDto> rewardCheckup(
            @PathVariable Long userId,
            @RequestParam(name = "checkupId") Long checkupId,
            @RequestParam OralCheckResultType result
    ) {
        WalletTransaction tx = walletService.giveReward(userId, checkupId, result);
        return ResponseEntity.ok(WalletTransactionDto.from(tx));
    }
}
