package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.dto.TokenTransferResponse;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.global.common.util.CryptoUtil;
import com.kaii.dentix.global.flask.client.FlaskClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWalletService {

    private final FlaskClient flaskClient;
    private final AdminWalletRepository adminWalletRepository;
    private final TokenContractRepository tokenContractRepository;
    private final UserWalletRepository userWalletRepository;
    private final CryptoUtil cryptoUtil;
    private final TokenLedgerRepository tokenLedgerRepository;
    /**
     * ✅ Flask에서 모든 주소별 잔액 조회 후, 관리자 지갑 잔액 업데이트
     */
    @Transactional
    public void syncAllWalletBalances() {
        // ✅ Flask 응답: [ [address, balance], ... ]
        List<List<Object>> balanceList = flaskClient.getBalanceList();

        for (List<Object> entry : balanceList) {
            if (entry.size() < 2) {
                log.warn("⚠️ 잘못된 데이터 형식: {}", entry);
                continue;
            }

            String address = String.valueOf(entry.get(0));
            Object balanceObj = entry.get(1);

            Long balance = 0L;
            if (balanceObj instanceof Number) {
                balance = ((Number) balanceObj).longValue();
            } else if (balanceObj instanceof String) {
                try {
                    balance = Long.parseLong((String) balanceObj);
                } catch (NumberFormatException ex) {
                    log.warn("⚠️ 잘못된 balance 값: {}", balanceObj);
                    continue;
                }
            }

            final Long finalBalance = balance;
            adminWalletRepository.findByAddress(address).ifPresent(wallet -> {
                wallet.updateBalance(finalBalance);
                log.info("✅ 잔액 동기화 완료: {} → {}", address, finalBalance);
            });
        }

        log.info("✅ 전체 관리자 지갑 잔액 동기화 완료 ({}개)", balanceList.size());
    }
    @Transactional
    public void issueTokenManually(Long userId, Long amount, String reason) {
        AdminWallet adminWallet = adminWalletRepository.findActiveWallet()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));

        UserWallet userWallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 존재하지 않습니다."));

        String privateKey = cryptoUtil.decrypt(adminWallet.getEncryptedPrivateKey());
        String contractAddr = adminWallet.getContract().getContractAddress();

        TokenTransferResponse res = flaskClient.transferToken(
                contractAddr,
                adminWallet.getAddress(),
                privateKey,
                userWallet.getWalletAddress(),
                amount
        );

        TokenLedger ledger = TokenLedger.builder()
                .contract(adminWallet.getContract())
                .fromAdminWallet(adminWallet)
                .toUserWallet(userWallet)
                .amount(BigDecimal.valueOf(amount))
                .type(TokenLedgerType.MANUAL)
                .status(TokenLedgerStatus.SUCCESS)
                .message(reason)
                .txHash("manual-" + System.currentTimeMillis())
                .build();

        tokenLedgerRepository.save(ledger);

        log.info("✅ 수동 토큰 발급 완료: userId={}, amount={}, reason={}", userId, amount, reason);
    }

}