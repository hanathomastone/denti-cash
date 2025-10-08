package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminWalletService;
import com.kaii.dentix.domain.admin.dto.statistic.AdminTokenTransferRequest;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepositoryCustom;
import com.kaii.dentix.domain.blockChain.token.dto.*;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.response.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {
//    private final AdminTokenService adminTokenLedgerService;
    private final AdminWalletService adminWalletService;
    private final TokenLedgerRepositoryCustom tokenLedgerRepositoryCustom;

    /**
     * ✅ [관리자 토큰 잔액 일괄 동기화]
     * POST /admin/wallet/sync-all
     */
    @PostMapping("/sync-all")
    public ResponseEntity<DataResponse<SyncBalanceResponseDto>> syncAllBalances() {
        try {
            adminWalletService.syncAllWalletBalances();

            SyncBalanceResponseDto response = SyncBalanceResponseDto.success(null);
            return ResponseEntity.ok(
                    new DataResponse<>(200, ResponseMessage.SUCCESS_MSG, response)
            );

        } catch (Exception e) {
            log.error("❌ 잔액 동기화 실패", e);
            throw new RuntimeException("잔액 동기화 실패: " + e.getMessage());
        }
    }

    /**
     * 🪙 수동 토큰 발급 (관리자 → 사용자)
     * POST /admin/wallet/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<DataResponse<TokenTransferResponseDto>> manualTransfer(
            @Valid @RequestBody AdminTokenTransferRequest request) {

        log.info("🪙 수동 토큰 발급 요청: userId={}, amount={}, reason={}",
                request.getUserId(), request.getAmount(), request.getReason());

        try {
            Map<String, Object> result = adminWalletService.issueTokenManually(request);
            TokenTransferResponseDto response = TokenTransferResponseDto.from(result);

            return ResponseEntity.ok(
                    new DataResponse<>(200, "토큰 발급이 완료되었습니다.", response)
            );
        } catch (Exception e) {
            log.error("❌ 토큰 발급 실패: userId={}", request.getUserId(), e);
            throw new RuntimeException("토큰 발급 실패: " + e.getMessage());
        }
    }

    /**
     * 💰 사용자 토큰 잔액 조회
     * GET /admin/wallet/balance/{userId}
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<DataResponse<UserBalanceResponseDto>> getUserBalance(
            @PathVariable Long userId) {
        try {
            Long balance = adminWalletService.getUserTokenBalance(userId);
            String address = adminWalletService.getUserWalletAddress(userId);

            UserBalanceResponseDto response = UserBalanceResponseDto.of(userId, balance, address);

            return ResponseEntity.ok(
                    new DataResponse<>(200, ResponseMessage.SUCCESS_MSG, response)
            );
        } catch (Exception e) {
            log.error("❌ 잔액 조회 실패: userId={}", userId, e);
            throw new RuntimeException("잔액 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 📜 사용자 토큰 거래 내역 조회
     * GET /admin/wallet/transactions/{userId}?page=0&size=20
     */
    @GetMapping("/transactions/{userId}")
    public ResponseEntity<DataResponse<TransactionHistoryResponseDto>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            Map<String, Object> result = adminWalletService.getUserTransactionHistory(
                    userId, page, size
            );

            TransactionHistoryResponseDto response = TransactionHistoryResponseDto.from(result);

            return ResponseEntity.ok(
                    new DataResponse<>(200, ResponseMessage.SUCCESS_MSG, response)
            );
        } catch (Exception e) {
            log.error("❌ 거래내역 조회 실패: userId={}", userId, e);
            throw new RuntimeException("거래내역 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 🪙 토큰 컨트랙트 생성 (관리자 전용)
     * POST /admin/wallet/token/create
     *
     * Request Body:
     * {
     *   "tokenName": "MyToken",
     *   "tokenSymbol": "MTK",
     *   "supply": 1000000
     * }
     *
     * Response:
     * {
     *   "success": true,
     *   "contractAddress": "0xabc...",
     *   "tokenName": "MyToken",
     *   "tokenSymbol": "MTK",
     *   "supply": 1000000,
     *   "createdAt": "2025-10-07T10:30:00"
     * }
     */
    @PostMapping("/token/create")
    public ResponseEntity<DataResponse<TokenCreateResponseDto>> createToken(
            @Valid @RequestBody AdminTokenCreateRequest request) {

        log.info("🪙 토큰 생성 요청: name={}, symbol={}, supply={}",
                request.getTokenName(), request.getTokenSymbol(), request.getSupply());

        try {
            TokenCreateResponseDto response = adminWalletService.createTokenContract(request);

            return ResponseEntity.ok(
                    new DataResponse<>(200, "토큰 생성이 완료되었습니다.", response)
            );
        } catch (Exception e) {
            log.error("❌ 토큰 생성 실패", e);
            throw new RuntimeException("토큰 생성 실패: " + e.getMessage());
        }
    }


    /**
     * ♻️ 토큰 회수 (Owner → Holder)
     */
    @PostMapping("/retrieve/{ledgerId}")
    public ResponseEntity<Map<String, Object>> retrieveToken(
            @PathVariable Long ledgerId,
            @RequestBody(required = false) Map<String, String> body
    ) {
        String reason = body != null ? body.getOrDefault("reason", "관리자 회수") : "관리자 회수";
        Map<String, Object> result = adminWalletService.retrieveToken(ledgerId, reason);
        return ResponseEntity.ok(result);
    }

    /**
     * 🪙 관리자 토큰 통계 (총 발급 / 잔여 / 지급)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTokenStatistics() {
        return ResponseEntity.ok(adminWalletService.getTokenStatistics());
    }

    /**
     * 📜 토큰 거래 내역 목록 조회
     */
    @PostMapping("/ledger/list")
    public ResponseEntity<Page<TokenLedgerResponse>> getLedgerList(
            @RequestBody AdminTokenLedgerListRequest request
    ) {
        Page<TokenLedgerResponse> result = adminWalletService.getLedgerList(request);
        return ResponseEntity.ok(result);
    }
}