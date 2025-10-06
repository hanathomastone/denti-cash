package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminWalletService;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.admin.dto.AdminCreateTokenResponse;
import com.kaii.dentix.domain.admin.dto.request.AdminTokenCreateRequest;
import com.kaii.dentix.domain.blockChain.token.dto.ManualTokenIssueRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.response.ResponseMessage;
import com.kaii.dentix.global.common.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {

    private final AdminWalletService adminWalletService;

    /**
     * ✅ [관리자 토큰 잔액 일괄 동기화]
     * Flask에서 전체 balance_list 받아와 DB 업데이트
     */
    @PostMapping("/sync-all")
    public String syncAllBalances() {
        adminWalletService.syncAllWalletBalances();
        return "✅ 모든 관리자 지갑 잔액이 Flask와 동기화되었습니다.";
    }
    @PostMapping("/manual-issue")
    public ResponseEntity<ResponseMessage> issueManualToken(@RequestBody ManualTokenIssueRequest request) {
        adminWalletService.issueTokenManually(request.getUserId(), request.getAmount(), request.getReason());
        return new SuccessResponse();
    }

}