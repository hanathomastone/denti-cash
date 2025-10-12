package com.kaii.dentix.domain.blockChain.wallet.application;

import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.domain.blockChain.wallet.dto.UserWalletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserWalletRepository userWalletRepository;

    public UserWalletResponse getWalletByUserId(Long userId) {
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        return new UserWalletResponse(wallet.getAddress(), wallet.getCreated().toString());
    }

}