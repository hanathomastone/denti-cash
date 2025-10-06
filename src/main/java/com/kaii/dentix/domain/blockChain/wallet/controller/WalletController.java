//package com.kaii.dentix.domain.blockChain.wallet.controller;
//
//
//import com.kaii.dentix.domain.type.TransactionPeriod;
//import com.kaii.dentix.domain.type.TransactionSort;
//import com.kaii.dentix.domain.type.TransactionTypeOption;
//import com.kaii.dentix.domain.type.oral.OralCheckResultType;
//import com.kaii.dentix.domain.blockChain.wallet.dto.WalletDto;
//import com.kaii.dentix.global.common.response.DataResponse;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import com.kaii.dentix.domain.blockChain.wallet.application.WalletService;
//
//import java.time.LocalDateTime;
//
//@RestController
//@RequestMapping("/wallet")
//@RequiredArgsConstructor
//public class WalletController {
//
//    private final WalletService walletService;
////    private final Wallet walletRepository;
//    @PostMapping("/{userId}/charge")
//    public ResponseEntity<WalletTransactionDto> charge(
//            @PathVariable Long userId,
//            @RequestParam Long amount,
//            @RequestParam(required = false) String description) {
//        return ResponseEntity.ok(
//                WalletTransactionDto.from(walletService.chargeWallet(userId, amount, description))
//        );
//    }
//
//    @PostMapping("/{userId}/use")
//    public ResponseEntity<WalletTransactionDto> use(
//            @PathVariable Long userId,
//            @RequestParam Long amount,
//            @RequestParam(required = false) String description) {
//        return ResponseEntity.ok(
//                WalletTransactionDto.from(walletService.useWallet(userId, amount, description))
//        );
//    }
//
//    @GetMapping("/{userId}/transactions")
//    public ResponseEntity<Page<WalletTransactionDto>> getTransactions(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "ALL") TransactionTypeOption transactionType,
//            @RequestParam(defaultValue = "ALL") TransactionPeriod period,
//            @RequestParam(defaultValue = "LATEST") TransactionSort sort,
//            Pageable pageable // ✅ page, size, sort 자동 매핑
//    ) {
//        Page<WalletTransactionDto> result = walletService.getTransactions(userId, transactionType, period, sort, pageable)
//                .map(WalletTransactionDto::from);
//
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping("/{userId}")
//    public ResponseEntity<WalletDto> getWallet(@PathVariable Long userId) {
//        return ResponseEntity.ok(
//                WalletDto.from(walletService.getWalletByUserId(userId))
//        );
//    }
//
//    @PostMapping("/{userId}/rewards/checkup")
//    public ResponseEntity<WalletTransactionDto> rewardCheckup(
//            @PathVariable Long userId,
//            @RequestParam(name = "checkupId") Long checkupId,
//            @RequestParam OralCheckResultType result
//    ) {
//        WalletTransaction tx = walletService.giveReward(userId, checkupId, result);
//        return ResponseEntity.ok(WalletTransactionDto.from(tx));
//    }
//
////    @GetMapping
////    public Page<WalletTransactionResponse> getTransactions(
////            @RequestParam(required = false) Long userId,
////            @RequestParam(required = false) String type,
////            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
////            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
////            @RequestParam(defaultValue = "0") int page,
////            @RequestParam(defaultValue = "10") int size
////    ) {
////        return walletService.getTransactions(userId, type, start, end, page, size);
////    }
//
//    /**
//     * ✅ 사용자별 거래내역 조회 (페이징 + 필터)
//     */
//    @GetMapping
//    public DataResponse<Page<WalletTransactionResponse>> getTransactions(
//            @RequestParam(required = false) Long userId,
//            @RequestParam(required = false) String type,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            LocalDateTime startDate,
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
//            LocalDateTime endDate,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        Page<WalletTransactionResponse> result = walletService.getTransactions(
//                userId, type, startDate, endDate, PageRequest.of(page, size)
//        );
//        return new DataResponse<>(result);
//    }
//}
////