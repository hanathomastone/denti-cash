package com.kaii.dentix.domain.admin.controller;

import com.kaii.dentix.domain.admin.application.AdminWalletService;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/wallet")
@RequiredArgsConstructor
public class AdminWalletController {

    private final AdminWalletService adminWalletService;

    // ✅ 어드민 지갑 생성
    @PostMapping("/create")
    public AdminWallet createAdminWallet() {
        return adminWalletService.createAdminWallet();
    }

    // ✅ 현재 활성화된 어드민 지갑 조회
    @GetMapping("/active")
    public AdminWallet getActiveAdminWallet() {
        return adminWalletService.getActiveAdminWallet();
    }
}