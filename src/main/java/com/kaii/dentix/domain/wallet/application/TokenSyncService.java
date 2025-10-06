package com.kaii.dentix.domain.wallet.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletTransactionRepository;
import com.kaii.dentix.domain.wallet.domain.Wallet;
import com.kaii.dentix.domain.wallet.dao.WalletRepository;
import com.kaii.dentix.infra.tokensync.TokenSyncClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenSyncService {

    private final TokenSyncClient client;
    private final WalletRepository walletRepository;
    private final AdminWalletTransactionRepository adminWalletTxRepository;

    /**
     * 단일 컨트랙트의 전체 홀더 잔액을 동기화
     */
    @Transactional
    public void syncBalancesForContract(String contractAddress) {
        log.info("[TokenSync] balance_list sync start - contract={}", contractAddress);
        Map<String, Object> balances = client.balanceList(contractAddress);
        if (balances == null || balances.isEmpty()) {
            log.warn("[TokenSync] empty response for contract={}", contractAddress);
            return;
        }
        balances.forEach((address, value) -> {
            try {
                Long newBalance = Long.valueOf(String.valueOf(value));
                walletRepository.findByAddress(address).ifPresent(wallet -> {
                    wallet.updateBalance(newBalance, "자동 동기화 (" + contractAddress + ")");
                });
            } catch (Exception e) {
                log.error("[TokenSync] parse/update fail addr={} val={}", address, value, e);
            }
        });
        log.info("[TokenSync] balance_list sync done - contract={}", contractAddress);
    }
    /**
     * 발행/거래로 사용된 모든 컨트랙트에 대해 일괄 동기화
     */
    @Transactional
    public void syncAllContracts() {
        List<String> contracts = adminWalletTxRepository.findDistinctContractAddresses();
        if (contracts.isEmpty()) {
            log.info("[TokenSync] no contracts to sync");
            return;
        }
        contracts.forEach(this::syncBalancesForContract);
    }

    /**
     * 스케줄러: 5분마다 전체 컨트랙트 잔액 동기화
     */
    @Scheduled(cron = "${token-sync.cron:0 */5 * * * *}")
    public void scheduledSync() {
        try {
            syncAllContracts();
        } catch (Exception e) {
            log.error("[TokenSync] scheduled sync error", e);
        }
    }
}