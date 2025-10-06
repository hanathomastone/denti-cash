package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.admin.dao.AdminWalletTransactionRepository;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.admin.domain.AdminWalletTransaction;
import com.kaii.dentix.domain.admin.dto.AdminCreateTokenResponse;
import com.kaii.dentix.domain.admin.dto.request.AdminTokenCreateRequest;
import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.domain.wallet.infra.WalletApiClient;
import com.kaii.dentix.global.common.response.DataResponse;
import com.kaii.dentix.global.common.util.CryptoUtil;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminWalletService {

    private final AdminWalletRepository adminWalletRepository;
    private final WalletApiClient walletApiClient;

    private final AdminWalletTransactionRepository adminWalletTransactionRepository;

    private static final String TOKEN_CREATE_URL = "http://220.149.235.79:5000/token/create";

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
    /**
     * 관리자 토큰 생성 (DataResponse 반환)
     */
    @Transactional
    public AdminCreateTokenResponse createToken(AdminTokenCreateRequest request, Long adminWalletId) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> body = new HashMap<>();
        body.put("token_name", request.getTokenName());
        body.put("token_symbol", request.getTokenSymbol());
        body.put("supply", request.getSupply());

        Map<String, Object> flaskResponse = restTemplate.postForObject(
                TOKEN_CREATE_URL, body, Map.class
        );

        String contractAddress = (String) flaskResponse.get("contract_address");
        if (contractAddress == null || contractAddress.isEmpty()) {
            throw new IllegalStateException("Flask 서버에서 contract_address를 반환하지 않았습니다.");
        }

        AdminWallet wallet = adminWalletRepository.findById(adminWalletId)
                .orElseThrow(() -> new IllegalArgumentException("AdminWallet을 찾을 수 없습니다."));

        AdminWalletTransaction transaction = AdminWalletTransaction.builder()
                .adminWallet(wallet)
                .transactionType(TransactionType.CHARGE)
                .amount(request.getSupply())
                .description("토큰 생성 (" + request.getTokenSymbol() + ")")
                .contractAddress(contractAddress)
                .build();

        adminWalletTransactionRepository.save(transaction);
        wallet.addBalance(request.getSupply());
        adminWalletRepository.save(wallet);

        return AdminCreateTokenResponse.builder()
                .contractAddress(contractAddress)
                .newBalance(wallet.getBalance())
                .build();
    }
}