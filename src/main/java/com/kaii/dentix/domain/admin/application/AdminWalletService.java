package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.wallet.infra.WalletApiClient;
import com.kaii.dentix.global.common.util.CryptoUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminWalletService {

    private final AdminWalletRepository adminWalletRepository;
    private final WalletApiClient walletApiClient;

    @Transactional
    public AdminWallet createAdminWallet() {
        String address = walletApiClient.createWalletAddress();
        String privateKey = walletApiClient.getPrivateKey(address);

        adminWalletRepository.findByActiveTrue().ifPresent(AdminWallet::deactivate);

        AdminWallet adminWallet = AdminWallet.builder()
                .address(address)
                .privateKey(CryptoUtil.encrypt(privateKey))
                .active(true)
                .build();

        return adminWalletRepository.save(adminWallet);
    }

    @Transactional(readOnly = true)
    public AdminWallet getActiveAdminWallet() {
        return adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));
    }
}