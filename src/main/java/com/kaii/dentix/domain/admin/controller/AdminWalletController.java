package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminWalletService;
import com.kaii.dentix.domain.admin.dto.AdminWalletSummaryDto;
import com.kaii.dentix.domain.admin.dto.AdminWalletSummaryNewDto;
import com.kaii.dentix.domain.admin.dto.statistic.AdminTokenTransferRequest;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepositoryCustom;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.dto.*;
import com.kaii.dentix.domain.blockChain.wallet.application.UserTokenLedgerService;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.response.ResponseMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {
    private final AdminWalletService adminWalletService;
    private final UserTokenLedgerService userTokenLedgerService;
    private final TokenLedgerRepositoryCustom tokenLedgerRepositoryCustom;

    @GetMapping("/users")
    public ResponseEntity<DataResponse<Page<TokenLedgerDto>>> getMyLedgerHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TokenLedgerDto> result = userTokenLedgerService.getAllUserLedgerHistory(page, size);
        return ResponseEntity.ok(new DataResponse<>(200, "사용자 거래내역 조회 성공", result));
    }

    @GetMapping("/ledger-history")
    public ResponseEntity<DataResponse<Page<TokenLedgerDto>>> getLedgerHistory(
            @RequestParam(required = false) String contractAddress,
            @RequestParam(defaultValue = "ALL") String category, // ISSUE, CHARGE, RECLAIM, ALL
            @RequestParam(defaultValue = "NEW") String sort,
            @RequestParam(required = false) String allDatePeriod,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TokenLedgerDto> result = userTokenLedgerService.getLedgerHistoryByCategory(
                contractAddress, category, sort, allDatePeriod, startDate, endDate, page, size
        );
        return ResponseEntity.ok(new DataResponse<>(200, "거래내역 조회 성공", result));
    }

    @GetMapping("/issue-reward")
    public ResponseEntity<DataResponse<Page<TokenLedgerDto>>> getIssueHistory(
            @RequestParam(required = false) String contractAddress,
            @RequestParam(defaultValue = "ISSUE_REWARD") String category,
            @RequestParam(defaultValue = "NEW") String sort,
            @RequestParam(required = false) String allDatePeriod,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TokenLedgerDto> result = userTokenLedgerService.getLedgerHistoryByCategory(
                contractAddress, category, sort, allDatePeriod, startDate, endDate, page, size
        );
        return ResponseEntity.ok(new DataResponse<>(200, "발행 내역 조회 성공", result));
    }
    /**
     * 충전 + 회수 통합 조회 API
     * category: CHARGE, RECLAIM, CHARGE_RECLAIM, ISSUE, ALL
     * sort: NEW (기본), OLD (오래된순)
     * allDatePeriod: TODAY, 1WEEK, 1MONTH, 3MONTH, ALL
     */
    @GetMapping("/charge-reclaim")
    public ResponseEntity<DataResponse<Page<TokenLedgerDto>>> getChargeReclaimHistory(
            @RequestParam(required = false) String contractAddress,
            @RequestParam(defaultValue = "CHARGE_RECLAIM") String category, // 기본값
            @RequestParam(defaultValue = "NEW") String sort,
            @RequestParam(required = false) String allDatePeriod,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TokenLedgerDto> result = userTokenLedgerService.getLedgerHistoryByCategory(
                contractAddress,
                category,    // CHARGE_RECLAIM → CHARGE + RECLAIM 둘 다 조회
                sort,
                allDatePeriod,
                startDate,
                endDate,
                page,
                size
        );

        return ResponseEntity.ok(new DataResponse<>(200, "충전/회수 내역 조회 성공", result));
    }

    @GetMapping("/by-contract")
    public ResponseEntity<DataResponse<Page<TokenLedgerDto>>> getUserLedgerByContract(
            @RequestParam String contractAddress,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<TokenLedgerDto> result = userTokenLedgerService.getLedgerByContract(contractAddress, page, size);
        return ResponseEntity.ok(new DataResponse<>(200, "선택한 거래주소 거래내역 조회 성공", result));
    }

    /**
     * [관리자 토큰 잔액 일괄 동기화]
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
            log.error("잔액 동기화 실패", e);
            throw new RuntimeException("잔액 동기화 실패: " + e.getMessage());
        }
    }

    /**
     * 수동 토큰 발급 (관리자 → 사용자)
     * POST /admin/wallet/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<DataResponse<TokenTransferResponseDto>> manualTransfer(
            @Valid @RequestBody AdminTokenTransferRequest request) {

        log.info("수동 토큰 발급 요청: userId={}, amount={}, reason={}",
                request.getUserId(), request.getAmount(), request.getReason());

        try {
            Map<String, Object> result = adminWalletService.issueTokenManually(request);
            TokenTransferResponseDto response = TokenTransferResponseDto.from(result);

            return ResponseEntity.ok(
                    new DataResponse<>(200, "토큰 발급이 완료되었습니다.", response)
            );
        } catch (Exception e) {
            log.error("토큰 발급 실패: userId={}", request.getUserId(), e);
            throw new RuntimeException("토큰 발급 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 토큰 잔액 조회
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
            log.error("잔액 조회 실패: userId={}", userId, e);
            throw new RuntimeException("잔액 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 사용자 토큰 거래 내역 조회
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
            log.error("거래내역 조회 실패: userId={}", userId, e);
            throw new RuntimeException("거래내역 조회 실패: " + e.getMessage());
        }
    }

    /**
     * 토큰 컨트랙트 생성 (관리자 전용)
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
    @PostMapping("/create")
    public ResponseEntity<DataResponse<TokenContract>> createToken(
            @RequestBody TokenCreateRequest request
    ) {
        TokenContract result = adminWalletService.createTokenContract(
                request.getTokenName(),
                request.getTokenSymbol(),
                request.getSupply()
        );
        return ResponseEntity.ok(new DataResponse<>(200, "토큰 생성 완료", result));
    }

    /**
     * 토큰 회수 (Owner → Holder)
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
     * 관리자 토큰 통계 (총 발급 / 잔여 / 지급)
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getTokenStatistics() {
        return ResponseEntity.ok(adminWalletService.getTokenStatistics());
    }

    /**
     * 드롭다운용 — 전체 컨트랙트 주소 목록
     */
    @GetMapping("/contracts")
    public ResponseEntity<DataResponse<List<String>>> getAllContractAddresses() {
        List<String> contracts = adminWalletService.getAllContractAddresses();
        return ResponseEntity.ok(new DataResponse<>(200, "컨트랙트 주소 목록 조회 성공", contracts));
    }

    /**
     * 상단 요약 (전체 or 특정 컨트랙트 기준)
     */
    @GetMapping("/summary")
    public ResponseEntity<DataResponse<AdminWalletSummaryNewDto>> getTokenSummary(
            @RequestParam(required = false) String contractAddress
    ) {
        AdminWalletSummaryNewDto summary = adminWalletService.getContractSummary(contractAddress);
        return ResponseEntity.ok(new DataResponse<>(200, "토큰 요약 조회 성공", summary));
    }

    /**
     * 거래내역 (전체 or 특정 컨트랙트 기준)
     */
    @GetMapping("/ledger")
    public ResponseEntity<DataResponse<List<TokenLedgerDto>>> getLedgerHistory(
            @RequestParam(required = false) String contractAddress
    ) {
        List<TokenLedgerDto> ledgers = adminWalletService.getLedgerHistoryByContract(contractAddress);
        return ResponseEntity.ok(new DataResponse<>(200, "거래내역 조회 성공", ledgers));
    }

    // 거래주소 일괄 회수
    @PostMapping("/reclaim/{contractId}")
    public ResponseEntity<String> reclaimAll(@PathVariable Long contractId) {
        adminWalletService.reclaimTokensByContract(contractId);
        return ResponseEntity.ok("회수 완료");
    }

    @GetMapping("/wallet-summary")
    public ResponseEntity<DataResponse<List<AdminWalletSummaryDto>>> getWalletSummary() {
        List<AdminWalletSummaryDto> result = adminWalletService.getWalletSummaries();
        return ResponseEntity.ok(new DataResponse<>(200, "거래주소별 잔고 조회 성공", result));
    }

    @PostMapping("/{walletId}/activate")
    public ResponseEntity<DataResponse<String>> activateWallet(@PathVariable Long walletId) {
        adminWalletService.activateWallet(walletId);
        return ResponseEntity.ok(new DataResponse<>(200, "지갑이 활성화되었습니다.", "SUCCESS"));
    }

    @PostMapping("/sync-balances")
    public ResponseEntity<DataResponse<String>> syncBalances() {
        int updated = adminWalletService.syncBalancesFromFlask();
        return ResponseEntity.ok(new DataResponse<>(200,
                "Flask 기준으로 DB 잔액 동기화 완료 (" + updated + "건)",
                "SUCCESS"));
    }
}