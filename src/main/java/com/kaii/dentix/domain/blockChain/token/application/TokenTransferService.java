package com.kaii.dentix.domain.blockChain.token.application;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.dto.TokenTransferRequest;
import com.kaii.dentix.domain.blockChain.token.dto.TokenTransferResponse;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.global.flask.client.FlaskClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenTransferService {
    private final FlaskClient flaskClient;
    private final TokenLedgerRepository tokenLedgerRepository;
    private final TokenContractRepository tokenContractRepository;
    private final AdminWalletRepository adminWalletRepository;
    private final UserWalletRepository userWalletRepository;

    @Transactional
    public TokenLedger transferToken(Long adminWalletId, Long userWalletId, Long amount) {
        AdminWallet adminWallet = adminWalletRepository.findById(adminWalletId)
                .orElseThrow(() -> new RuntimeException("관리자 지갑을 찾을 수 없습니다."));
        UserWallet userWallet = userWalletRepository.findById(userWalletId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑을 찾을 수 없습니다."));
        TokenContract contract = adminWallet.getContract();

        // ✅ Flask 요청 객체 구성
        TokenTransferRequest req = new TokenTransferRequest();
        req.setContractAddress(contract.getContractAddress());
        req.setSender(adminWallet.getAddress());
        req.setSenderPrivateKey(adminWallet.getEncryptedPrivateKey()); // ※ 복호화 필요 시 CryptoUtil 사용
        req.setReceiver(userWallet.getWalletAddress());
        req.setAmount(amount);

        // ✅ Flask로 전송
        TokenTransferResponse res = flaskClient.transferToken(req);

        // ✅ Ledger 기록
        TokenLedger ledger = TokenLedger.builder()
                .contract(contract)
                .fromAdminWallet(adminWallet)
                .toUserWallet(userWallet)
                .txHash(res.getDate())  // Flask가 txHash를 안 주므로 임시로 Date 기록
                .amount(BigDecimal.valueOf(amount))
                .type(TokenLedgerType.REWARD)
                .status(TokenLedgerStatus.SUCCESS)
                .message("Flask transfer 성공")
                .build();

        tokenLedgerRepository.save(ledger);

        log.info("✅ Ledger 기록 완료: {} -> {} ({}개)", adminWallet.getAddress(), userWallet.getWalletAddress(), amount);
        return ledger;
    }
}
