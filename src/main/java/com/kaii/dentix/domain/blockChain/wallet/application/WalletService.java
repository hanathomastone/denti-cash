package com.kaii.dentix.domain.blockChain.wallet.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.util.CryptoUtil;
import com.kaii.dentix.global.flask.client.FlaskClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigDecimal;
import java.util.Map;

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
                    .walletAddress(address)
                    .encryptedPrivateKey(encryptedPrivateKey)
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
    /**
     * 관리자 → 사용자 토큰 전송 (리워드 지급)
     */
    @Transactional
    public void transferRewardToUser(Long userId, Long amount) {
        // ✅ 1. 관리자 지갑 찾기
        AdminWallet adminWallet = adminWalletRepository.findActiveWallet()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));

        // ✅ 2. 사용자 지갑 찾기
        UserWallet userWallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        // ✅ 3. 토큰 컨트랙트
        TokenContract contract = adminWallet.getContract();

        // ✅ 4. 관리자 프라이빗키 복호화
        String privateKey = cryptoUtil.decrypt(adminWallet.getEncryptedPrivateKey());

        // ✅ 5. Flask 호출
        Map<String, Object> result = flaskClient.transferToken(
                contract.getContractAddress(),
                adminWallet.getAddress(),
                privateKey,
                userWallet.getWalletAddress(),
                amount
        );

        // ✅ 6. 거래 내역 저장
        TokenLedger ledger = TokenLedger.builder()
                .contract(contract)
                .fromAdminWallet(adminWallet)
                .toUserWallet(userWallet)
                .txHash(result.getOrDefault("Date", "N/A").toString()) // Flask는 txHash 대신 Date 리턴하므로
                .amount(BigDecimal.valueOf(amount))
                .type(TokenLedgerType.REWARD)
                .status(TokenLedgerStatus.SUCCESS)
                .message("Flask 전송 성공")
                .build();

        tokenLedgerRepository.save(ledger);

        // ✅ 7. 잔액 갱신 (임시로 로컬 반영)
        adminWallet.subtractBalance(amount);
        userWallet.addBalance(amount);

        log.info("✅ 사용자({})에게 {} 토큰 전송 완료", userId, amount);
    }


}