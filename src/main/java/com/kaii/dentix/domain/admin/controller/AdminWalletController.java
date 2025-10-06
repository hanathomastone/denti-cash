package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminWalletService;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.admin.dto.AdminCreateTokenResponse;
import com.kaii.dentix.domain.admin.dto.request.AdminTokenCreateRequest;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.response.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {

    private final AdminWalletService adminWalletService;

    // 어드민 지갑 생성
    @PostMapping("/create/address")
    public AdminWallet createAdminWallet() {
        return adminWalletService.createAdminWallet();
    }

    // 현재 활성화된 어드민 지갑 조회
    @GetMapping("/active")
    public AdminWallet getActiveAdminWallet() {
        return adminWalletService.getActiveAdminWallet();
    }

    @PostMapping("/create/token")
    public AdminCreateTokenResponse createToken(
            @RequestBody AdminTokenCreateRequest request,
            @RequestParam Long adminWalletId
    ) {
        return adminWalletService.createToken(request, adminWalletId);
    }
}