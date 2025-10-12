package com.kaii.dentix.domain.admin.application;

import com.kaii.dentix.domain.admin.dao.AdminWalletRepository;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.admin.dto.AdminWalletSummaryDto;
import com.kaii.dentix.domain.admin.dto.AdminWalletSummaryNewDto;
import com.kaii.dentix.domain.admin.dto.statistic.AdminTokenTransferRequest;
import com.kaii.dentix.domain.blockChain.token.dao.TokenContractRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepository;
import com.kaii.dentix.domain.blockChain.token.dao.TokenLedgerRepositoryCustom;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.dto.*;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerSourceType;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.dao.UserWalletRepository;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.flask.client.FlaskClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminWalletService {

    private final FlaskClient flaskClient;
    private final AdminWalletRepository adminWalletRepository;
    private final UserRepository userRepository;
    private final UserWalletRepository userWalletRepository;
    private final TokenLedgerRepository tokenLedgerRepository;
    private final TokenContractRepository tokenContractRepository;

    /**
     * Flask에서 모든 주소별 잔액 조회 후, 지갑 잔액 업데이트
     */
    @Transactional
    public void syncAllWalletBalances() {
        log.info("🔄 전체 지갑 잔액 동기화 시작");

        int successCount = 0;
        int failCount = 0;

        try {
            List<List<Object>> balanceList = flaskClient.getBalanceList();

            if (balanceList == null || balanceList.isEmpty()) {
                log.warn("⚠️ Flask로부터 받은 잔액 목록이 비어 있습니다. 동기화 중단.");
                return;
            }

            for (List<Object> entry : balanceList) {
                try {
                    if (entry == null || entry.size() < 2) {
                        log.warn("⚠️ 잘못된 데이터 형식: {}", entry);
                        failCount++;
                        continue;
                    }

                    String address = String.valueOf(entry.get(0));
                    Long balance = parseBalance(entry.get(1));

                    if (balance == null) {
                        log.warn("⚠️ 잔액 파싱 실패: {}", entry.get(1));
                        failCount++;
                        continue;
                    }

                    // 관리자 지갑 업데이트
                    adminWalletRepository.findByAddress(address).ifPresent(wallet -> {
                        wallet.updateBalance(balance);
                        log.debug("관리자 잔액 동기화: {} → {}", address, balance);
                    });

                    // 사용자 지갑 업데이트
                    userWalletRepository.findByAddress(address).ifPresent(wallet -> {
                        wallet.updateBalance(balance);
                        log.debug("사용자 잔액 동기화: {} → {}", address, balance);
                    });

                    successCount++;

                } catch (Exception innerEx) {
                    log.error("개별 주소 동기화 실패: {}", entry, innerEx);
                    failCount++;
                }
            }

            log.info("전체 지갑 잔액 동기화 완료 (성공: {}, 실패: {})", successCount, failCount);

        } catch (Exception e) {
            log.error("Flask 잔액 조회 실패: {}", e.getMessage(), e);
            //전체 실패시에도 프로그램 중단 대신 로그만 남김
        }
    }
    /**
     * 수동 토큰 발급 (관리자 → 사용자)
     */
    @Transactional
    public Map<String, Object> issueTokenManually(AdminTokenTransferRequest request) {
        log.info("토큰 수동 발급 시작: userId={}, amount={}, reason={}",
                request.getUserLoginIdentifier(), request.getAmount(), request.getReason());

        AdminWallet adminWallet = adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));
        User user = userRepository.findByUserLoginIdentifier(request.getUserLoginIdentifier())
                .orElseThrow(() -> new RuntimeException("해당 로그인 식별자로 사용자를 찾을 수 없습니다: "
                        + request.getUserLoginIdentifier()));

        //사용자 지갑 찾기
        UserWallet userWallet = userWalletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("해당 사용자의 지갑이 존재하지 않습니다."));

        if (!userWallet.isActive()) {
            throw new RuntimeException("비활성화된 지갑입니다.");
        }

        log.info("발급 정보 - Admin: {}, User: {}, Amount: {}",
                adminWallet.getAddress(), userWallet.getAddress(), request.getAmount());

        if (adminWallet.getBalance() < request.getAmount()) {
            throw new RuntimeException(
                    String.format("관리자 지갑 잔액 부족: 현재 %d, 필요 %d",
                            adminWallet.getBalance(), request.getAmount())
            );
        }

        TokenLedger ledger = TokenLedger.builder()
                .contract(adminWallet.getContract())
                .senderAdminWallet(adminWallet)
                .receiverUserWallet(userWallet)
                .amount(request.getAmount())
                .type(TokenLedgerType.REWARD)
                .status(TokenLedgerStatus.PENDING)
                .sourceType(TokenLedgerSourceType.MANUAL)
                .message(request.getReason())
                .build();
        tokenLedgerRepository.save(ledger);

        Map<String, Object> result = new HashMap<>();

        try {
            String plainPrivateKey = adminWallet.getPrivateKey();
            Long amountToTransfer = request.getAmount();

            Map<String, Object> flaskResponse = flaskClient.transferToken(
                    adminWallet.getAddress(),
                    plainPrivateKey,
                    userWallet.getAddress(),
                    amountToTransfer
            );

            ledger.markSuccess("전송 성공");
            tokenLedgerRepository.save(ledger);

            Long newAdminBalance = adminWallet.getBalance() - request.getAmount();
            adminWallet.updateBalance(newAdminBalance);
            userWallet.addBalance(request.getAmount());

            log.info("토큰 수동 발급 성공 | From={} → To={} | Amount={} | LedgerId={}",
                    adminWallet.getAddress(), userWallet.getAddress(),
                    request.getAmount(), ledger.getId());

            result.put("success", true);
            result.put("ledgerId", ledger.getId());
            result.put("fromAddress", adminWallet.getAddress());
            result.put("toAddress", userWallet.getAddress());
            result.put("amount", request.getAmount());
            result.put("reason", request.getReason());
            result.put("flaskResponse", flaskResponse);
            result.put("timestamp", LocalDateTime.now());

            return result;

        } catch (Exception e) {
            ledger.markFailed("전송 실패: " + e.getMessage());
            tokenLedgerRepository.save(ledger);

            log.error("토큰 전송 실패 | LedgerId={}", ledger.getId(), e);

            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("ledgerId", ledger.getId());

            throw new RuntimeException("토큰 전송 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 사용자 토큰 잔액 조회
     */
    public Long getUserTokenBalance(Long userId) {
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 없습니다."));

        return wallet.getBalance();
    }

    /**
     * 사용자 지갑 주소 조회
     */
    public String getUserWalletAddress(Long userId) {
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 없습니다."));

        return wallet.getAddress();
    }

    /**
     * 사용자 토큰 거래 내역 조회
     */
    public Map<String, Object> getUserTransactionHistory(Long userId, int page, int size) {
        UserWallet wallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 없습니다."));

        PageRequest pageRequest = PageRequest.of(page, size);

        // 지갑 주소로 발신/수신 모든 거래 조회
        Page<TokenLedger> ledgers = tokenLedgerRepository.findByWalletAddress(
                wallet.getAddress(),
                pageRequest
        );

        Map<String, Object> result = new HashMap<>();
        result.put("transactions", ledgers.getContent());
        result.put("totalElements", ledgers.getTotalElements());
        result.put("totalPages", ledgers.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);

        return result;
    }

    /**
     * Balance 파싱 유틸리티
     */
    private Long parseBalance(Object balanceObj) {
        if (balanceObj instanceof Number) {
            return ((Number) balanceObj).longValue();
        } else if (balanceObj instanceof String) {
            try {
                return Long.parseLong((String) balanceObj);
            } catch (NumberFormatException ex) {
                log.warn("잘못된 balance 값: {}", balanceObj);
                return null;
            }
        }
        return null;
    }

    @Transactional
    public TokenContract createTokenContract(String tokenName, String tokenSymbol, Long supply) {
        log.info("🚀 토큰 생성 요청: name={}, symbol={}, supply={}", tokenName, tokenSymbol, supply);

        // 1️⃣ Flask로 요청
        Map<String, Object> response = flaskClient.createToken(tokenName, tokenSymbol, supply);
        String contractAddress = (String) response.getOrDefault("contract address", "");

        if (contractAddress.isBlank()) {
            throw new RuntimeException("Flask 응답에 contract_address가 없습니다.");
        }

        // 2️⃣ 기존 활성 컨트랙트 비활성화
        tokenContractRepository.findActiveContract().ifPresent(contract -> {
            contract.deactivate();
            tokenContractRepository.save(contract);
            log.info("⛔ 기존 컨트랙트 비활성화: {}", contract.getContractAddress());
        });

        // 3️⃣ 새 컨트랙트 저장
        TokenContract tokenContract = TokenContract.builder()
                .contractAddress(contractAddress)
                .tokenName(tokenName)
                .tokenSymbol(tokenSymbol)
                .supply(supply)
                .decimals(18)
                .active(true)
                .build();
        tokenContractRepository.save(tokenContract);

        // 4️⃣ 관리자 지갑 연결 및 잔액 갱신
        AdminWallet wallet = adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));
        wallet.updateContract(tokenContract);
        wallet.updateBalance(supply);

        adminWalletRepository.save(wallet);
        log.info("관리자 지갑 컨트랙트 연결 완료: {}", tokenContract.getContractAddress());

        return tokenContract;
    }

    @Transactional
    public Map<String, Object> retrieveToken(Long ledgerId, String reason) {
        // 1. 회수 대상 거래 조회
        TokenLedger target = tokenLedgerRepository.findById(ledgerId)
                .orElseThrow(() -> new RuntimeException("회수 대상 거래를 찾을 수 없습니다."));

        UserWallet receiverWallet = target.getReceiverUserWallet();
        if (receiverWallet == null) {
            throw new RuntimeException("수신자 지갑이 존재하지 않습니다.");
        }

        // 2. 관리자 지갑 조회
        AdminWallet adminWallet = adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));

        // 3. Flask 서버로 회수 요청 (사용자 → 관리자)
        Map<String, Object> body = Map.of(
                "contract_address", tokenContractRepository.findActiveContract()
                        .map(TokenContract::getContractAddress)
                        .orElseThrow(() -> new RuntimeException("활성화된 토큰 컨트랙트가 없습니다.")),
                "sender", receiverWallet.getAddress(),
                "sender_private_key", receiverWallet.getPrivateKey(),
                "receiver", adminWallet.getAddress(),
                "amount", target.getAmount()
        );
        flaskClient.transferToken(body);

        // 4. Ledger 기록
        TokenLedger retrieveLedger = TokenLedger.builder()
                .contract(adminWallet.getContract())
                .type(TokenLedgerType.RETRIEVE)
                .status(TokenLedgerStatus.SUCCESS)
                .sourceType(TokenLedgerSourceType.RETRIEVE)
                .amount(target.getAmount())
                .message("관리자 회수 (" + adminWallet.getAddress() + "): " + reason)
                .senderUserWallet(receiverWallet)
                .receiverUserWallet(null)
                .build();
        tokenLedgerRepository.save(retrieveLedger);

        // 5. 잔액 갱신
        receiverWallet.subtractBalance(target.getAmount());  // 사용자 차감
        adminWallet.addBalance(target.getAmount());          // 관리자 증가

        // 6. 응답 반환
        return Map.of(
                "message", "토큰 회수가 완료되었습니다.",
                "amount", target.getAmount(),
                "receiver", receiverWallet.getUser().getUserName(),
                "status", "SUCCESS"
        );
    }

    @Transactional
    public void giveReward(Long userId, Long oralCheckId, String resultType, int rewardAmount) {
        // 1. 기본 객체 조회
        TokenContract contract = tokenContractRepository.findActiveContract()
                .orElseThrow(() -> new RuntimeException("활성화된 토큰 컨트랙트가 없습니다."));
        AdminWallet adminWallet = adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성 관리자 지갑이 없습니다."));
        UserWallet userWallet = userWalletRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자 지갑이 없습니다."));

        // 2. 관리자 잔액 확인
        if (adminWallet.getBalance() < rewardAmount) {
            throw new RuntimeException("관리자 지갑 잔액 부족으로 보상 지급 실패");
        }

        // 3. 중복 지급 방지 (oralCheckId 기준)
        String rewardKey = "ORALCHECK-" + oralCheckId;
        if (tokenLedgerRepository.existsByReceiverUserWalletAndTypeAndMessage(
                userWallet, TokenLedgerType.REWARD, rewardKey)) {
            throw new IllegalStateException("이미 리워드 지급된 구강검진입니다.");
        }

        // 4. 사용자 지갑에 컨트랙트 연결 없을 경우 업데이트
        if (userWallet.getContract() == null) {
            userWallet.setContract(contract);
            userWalletRepository.save(userWallet);
        }

        // 5. 표시용 상태 이름 (한글 변환)
        String displayStatus = switch (resultType) {
            case "HEALTHY" -> "건강";
            case "GOOD" -> "양호";
            case "CAUTION" -> "주의";
            case "RISK" -> "위험";
            default -> "기타";
        };

        // 6. 사용자에게 보여질 메시지 (화면용)
        String displayMessage = "칫솔질 리워드(" + displayStatus + ")";

        // 7. Ledger 생성
        TokenLedger ledger = TokenLedger.builder()
                .contract(contract) // ❗ 반드시 토큰 컨트랙트 사용
                .senderAdminWallet(adminWallet)
                .receiverUserWallet(userWallet)
                .amount((long) rewardAmount)
                .type(TokenLedgerType.REWARD)
                .status(TokenLedgerStatus.PENDING)
                .sourceType(TokenLedgerSourceType.AUTO)
                .message(rewardKey) // 내부 중복 체크용 (ORALCHECK-123)
                .build();
        tokenLedgerRepository.save(ledger);

        try {
            // 8. Flask 전송
            Map<String, Object> flaskResponse = flaskClient.transferToken(
                    adminWallet.getAddress(),
                    adminWallet.getPrivateKey(),
                    userWallet.getAddress(),
                    (long) rewardAmount
            );

            // 9. 성공 시 Ledger 업데이트
            ledger.markSuccess(displayMessage); // ← “칫솔질 리워드(양호)” 로 메시지 갱신
            tokenLedgerRepository.save(ledger);

            // 10. 잔액 갱신
            adminWallet.subtractBalance((long) rewardAmount);
            userWallet.addBalance((long) rewardAmount);

            log.info("🎉 구강검진 리워드 지급 성공: userId={}, result={}, message={}, amount={}",
                    userId, resultType, displayMessage, rewardAmount);

        } catch (Exception e) {
            ledger.markFailed("보상 지급 실패: " + e.getMessage());
            tokenLedgerRepository.save(ledger);
            throw new RuntimeException("Flask 연동 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 관리자 토큰 통계 조회
     */
    public Map<String, Object> getTokenStatistics() {
        var adminWallet = adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));

        BigDecimal totalIssued = tokenLedgerRepository.sumAmountByType(TokenLedgerType.ISSUE)
                .orElse(BigDecimal.ZERO);
        BigDecimal distributed = tokenLedgerRepository.sumAmountByType(TokenLedgerType.TRANSFER)
                .orElse(BigDecimal.ZERO)
                .add(tokenLedgerRepository.sumAmountByType(TokenLedgerType.MANUAL).orElse(BigDecimal.ZERO));
        Long remaining = adminWallet.getBalance();

        Map<String, Object> result = new HashMap<>();
        result.put("totalIssuedToken", totalIssued);
        result.put("remainingToken", remaining);
        result.put("distributedToken", distributed);

        return result;
    }

    // 관리자 거래내역 조회
    @Transactional
    public List<AdminTokenLedgerDto> getAdminLedgers(String type, String period) {
        LocalDateTime fromDateTime = switch (period != null ? period.toUpperCase() : "") {
            case "1D" -> LocalDateTime.now().minusDays(1);
            case "3D" -> LocalDateTime.now().minusDays(3);
            case "7D" -> LocalDateTime.now().minusDays(7);
            default -> null;
        };

        Date fromDate = (fromDateTime != null)
                ? Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant())
                : null;

        TokenLedgerType ledgerType = type != null ? TokenLedgerType.valueOf(type.toUpperCase()) : null;
        List<TokenLedger> ledgers = tokenLedgerRepository.findAllByFilter(ledgerType, fromDate);
        return ledgers.stream().map(AdminTokenLedgerDto::from).toList();
    }

    // 거래주소 기준 일괄 회수
    @Transactional
    public void reclaimTokensByContract(Long contractId) {
        TokenContract contract = tokenContractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("해당 계약이 존재하지 않습니다."));
        AdminWallet adminWallet = adminWalletRepository.findByActiveTrue()
                .orElseThrow(() -> new RuntimeException("활성화된 관리자 지갑이 없습니다."));

        List<TokenLedger> rewards = tokenLedgerRepository
                .findAllByContractAndType(contract, TokenLedgerType.REWARD);

        for (TokenLedger ledger : rewards) {
            UserWallet userWallet = ledger.getReceiverUserWallet();
            Long amount = ledger.getAmount();

            if (userWallet.getBalance() < amount) continue; // 이미 사용한 토큰 skip

            userWallet.subtractBalance(amount);
            adminWallet.addBalance(amount);

            TokenLedger reclaimLedger = TokenLedger.builder()
                    .contract(contract)
                    .senderUserWallet(userWallet)
                    .receiverAdminWallet(adminWallet)
                    .amount(amount)
                    .type(TokenLedgerType.RECLAIM)
                    .sourceType(TokenLedgerSourceType.SYSTEM)
                    .status(TokenLedgerStatus.SUCCESS)
                    .message("거래주소 일괄 회수")
                    .build();
            tokenLedgerRepository.save(reclaimLedger);
        }
    }

    // 잔액 부족 시 다른 관리자 지갑에서 충전
    private void rechargeFromOtherWallets(AdminWallet targetWallet, Long requiredAmount) {
        List<AdminWallet> others = adminWalletRepository.findAll().stream()
                .filter(w -> !w.getAdminWalletId().equals(targetWallet.getAdminWalletId()))
                .sorted((a, b) -> Long.compare(b.getBalance(), a.getBalance()))
                .toList();

        long remaining = requiredAmount - targetWallet.getBalance();

        for (AdminWallet source : others) {
            if (remaining <= 0) break;
            long transferable = Math.min(source.getBalance(), remaining);

            if (transferable > 0) {
                source.subtractBalance(transferable);
                targetWallet.addBalance(transferable);

                TokenLedger transferLedger = TokenLedger.builder()
                        .senderAdminWallet(source)
                        .receiverAdminWallet(targetWallet)
                        .amount(transferable)
                        .type(TokenLedgerType.ADMIN_TRANSFER)
                        .sourceType(TokenLedgerSourceType.AUTO)
                        .status(TokenLedgerStatus.SUCCESS)
                        .message("자동 충전")
                        .build();
                tokenLedgerRepository.save(transferLedger);
                remaining -= transferable;
            }
        }

        if (remaining > 0) {
            throw new RuntimeException("모든 관리자 지갑에서 충전 불가 — 잔액 부족");
        }
    }

    @Transactional
    public List<AdminWalletSummaryDto> getWalletSummaries() {
        return adminWalletRepository.findWalletSummaries();
    }

    @Transactional
    public void activateWallet(Long walletId) {
        log.info("관리자 지갑 활성화 요청: walletId={}", walletId);

        // 기존 활성 지갑 모두 비활성화
        List<AdminWallet> activeWallets = adminWalletRepository.findAllByActiveTrue();
        for (AdminWallet w : activeWallets) {
            w.deactivate();
        }

        //선택한 지갑 활성화
        AdminWallet wallet = adminWalletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("해당 ID의 관리자 지갑이 존재하지 않습니다."));

        wallet.activate();
        log.info("활성화된 관리자 지갑: {}", wallet.getAddress());
    }

    /**
     * Flask 잔액 기준으로 DB balance 자동 동기화
     */
    @Transactional
    public int syncBalancesFromFlask() {
        log.info("Flask → DB 지갑 잔액 동기화 시작");

        List<List<Object>> flaskData = flaskClient.getBalanceList();
        if (flaskData == null || flaskData.isEmpty()) {
            log.warn("Flask로부터 받은 잔액 데이터가 비어 있습니다.");
            return 0;
        }

        int updateCount = 0;

        for (List<Object> entry : flaskData) {
            if (entry.size() < 2) continue;

            String address = String.valueOf(entry.get(0));
            Long flaskBalance = Long.parseLong(entry.get(1).toString());

            // 관리자 지갑 업데이트
            adminWalletRepository.findByAddress(address).ifPresent(wallet -> {
                if (!Objects.equals(wallet.getBalance(), flaskBalance)) {
                    log.info("[Admin] 잔액 업데이트: {} ({} → {})",
                            wallet.getAddress(), wallet.getBalance(), flaskBalance);
                    wallet.updateBalance(flaskBalance);
                }
            });

            // 사용자 지갑 업데이트
            userWalletRepository.findByAddress(address).ifPresent(wallet -> {
                if (!Objects.equals(wallet.getBalance(), flaskBalance)) {
                    log.info("[User] 잔액 업데이트: {} ({} → {})",
                            wallet.getAddress(), wallet.getBalance(), flaskBalance);
                    wallet.updateBalance(flaskBalance);
                }
            });

            updateCount++;
        }

        log.info("Flask → DB 잔액 동기화 완료 (총 {}건 처리)", updateCount);
        return updateCount;
    }

    /**
     * 컨트랙트 주소 목록 (드롭다운용)
     */
    public List<String> getAllContractAddresses() {
        return tokenContractRepository.findAll()
                .stream()
                .map(TokenContract::getContractAddress)
                .toList();
    }

    /**
     * 컨트랙트 기준 요약
     */
    public AdminWalletSummaryNewDto getContractSummary(String contractAddress) {
        if (contractAddress == null || contractAddress.isBlank()) {
            Long totalBalance = Optional.ofNullable(tokenLedgerRepository.sumAllBalances()).orElse(0L);
            Long totalIssued = Optional.ofNullable(tokenLedgerRepository.sumAllIssuedTokens()).orElse(0L);
            Long totalRemain = totalBalance - totalIssued;
            return new AdminWalletSummaryNewDto(totalBalance, totalIssued, totalRemain);
        } else {
            Long totalBalance = Optional.ofNullable(tokenLedgerRepository.sumBalanceByContract(contractAddress)).orElse(0L);
            Long totalIssued = Optional.ofNullable(tokenLedgerRepository.sumIssuedByContract(contractAddress)).orElse(0L);
            Long totalRemain = totalBalance - totalIssued;
            return new AdminWalletSummaryNewDto(totalBalance, totalIssued, totalRemain);
        }
    }

    /**
     * 컨트랙트 기준 거래내역
     */
    public List<TokenLedgerDto> getLedgerHistoryByContract(String contractAddress) {
        if (contractAddress == null || contractAddress.isBlank()) {
            return tokenLedgerRepository.findAllByOrderByCreatedDesc()
                    .stream().map(TokenLedgerDto::from).toList();
        } else {
            return tokenLedgerRepository.findAllByContractAddress(contractAddress)
                    .stream().map(TokenLedgerDto::from).toList();
        }
    }
}