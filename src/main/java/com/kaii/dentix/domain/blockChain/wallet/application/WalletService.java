package com.kaii.dentix.domain.blockChain.wallet.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.domain.blockChain.wallet.dto.UserWalletResponse;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.util.CryptoUtil;
import com.kaii.dentix.global.flask.client.FlaskClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserWalletRepository userWalletRepository;
    private final TokenContractRepository tokenContractRepository;
    private final CryptoUtil cryptoUtil;
    private final AdminWalletRepository adminWalletRepository;
    private final FlaskClient flaskClient;
    private final TokenLedgerRepository tokenLedgerRepository;

    public UserWalletResponse getWalletByUserId(Long userId) {
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        return new UserWalletResponse(wallet.getAddress(), wallet.getCreated().toString());
    }
    /**
     * 새 회원가입 시 사용자 지갑 생성
     */
    public UserWallet createUserWallet(User user) {
        try {
            TokenContract defaultContract = tokenContractRepository.findActiveContract()
                    .orElseThrow(() -> new RuntimeException("활성화된 토큰 컨트랙트가 없습니다."));

            ECKeyPair keyPair = Keys.createEcKeyPair();
            String privateKey = keyPair.getPrivateKey().toString(16);
            String address = "0x" + Keys.getAddress(keyPair.getPublicKey());
            String encryptedPrivateKey = cryptoUtil.encrypt(privateKey);

            UserWallet wallet = UserWallet.builder()
                    .user(user) // ✅ 이제 존재함
                    .address(address)
                    .privateKey(encryptedPrivateKey)
                    .balance(0L)
                    .contract(defaultContract)
                    .active(true)
                    .build();

            userWalletRepository.save(wallet);

            log.info("✅ 새 사용자 지갑 생성 완료 - userId={}, address={}", user.getUserId(), address);
            return wallet;
        } catch (Exception e) {
            log.error("❌ 사용자 지갑 생성 실패", e);
            throw new RuntimeException("지갑 생성 실패", e);
        }
    }


}