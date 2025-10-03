package com.kaii.dentix.domain.wallet.application;
import com.kaii.dentix.domain.admin.application.AdminWalletService;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.oralCheck.dao.OralCheckRepository;
import com.kaii.dentix.domain.oralCheck.domain.OralCheck;
import com.kaii.dentix.domain.type.TransactionPeriod;
import com.kaii.dentix.domain.type.TransactionSort;
import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.domain.type.TransactionTypeOption;
import com.kaii.dentix.domain.type.oral.OralCheckResultType;
import com.kaii.dentix.domain.userPrivateKey.dao.UserPrivateKeyRepository;
import com.kaii.dentix.domain.userPrivateKey.domain.UserPrivateKey;
import com.kaii.dentix.domain.wallet.dao.WalletTransactionRepository;
import com.kaii.dentix.domain.wallet.infra.WalletApiClient;
import com.kaii.dentix.global.common.util.CryptoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kaii.dentix.domain.user.dao.UserRepository;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.wallet.dao.WalletRepository;
import com.kaii.dentix.domain.wallet.domain.Wallet;
import com.kaii.dentix.domain.wallet.domain.WalletTransaction;


import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;
    private final WalletApiClient walletApiClient;
    private final WalletTransactionRepository walletTransactionRepository;
    private final UserRepository userRepository;
    private final OralCheckRepository oralCheckRepository;
    private final UserPrivateKeyRepository userPrivateKeyRepository;
    private final AdminWalletService adminWalletService;

    private static final String CONTRACT_ADDRESS = "0xYourTokenContractAddress"; // 환경 변수로 관리 권장

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("해당 사용자의 지갑이 존재하지 않습니다."));
    }

    @Transactional
    public WalletTransaction chargeWallet(Long userId, Long amount, String description) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }

        Wallet wallet = getWalletByUserId(userId);
        if (wallet == null) {
            throw new RuntimeException("사용자 지갑을 찾을 수 없습니다.");
        }

        WalletTransaction tx = wallet.charge(amount, description);
        walletRepository.save(wallet);
        return tx;
    }

    @Transactional
    public WalletTransaction useWallet(Long userId, Long amount, String description) {
        Wallet wallet = getWalletByUserId(userId);
        WalletTransaction tx = wallet.use(amount, description); // ✅ 도메인 메서드 호출
        walletRepository.save(wallet); // tx도 cascade로 같이 저장됨
        return tx;
    }
    // 회원가입 시 지갑 생성
    @Transactional
    public Wallet createWalletForUser(User user) {
        // 1. 외부 API 호출 → 지갑 address 발급
        String walletAddress = walletApiClient.createWalletAddress();

        // 2. Wallet 엔티티 생성 & 저장
        Wallet wallet = Wallet.builder()
                .user(user)
                .address(walletAddress)
                .balance(0L)
                .build();
        walletRepository.save(wallet);

        // 3. 외부 API 호출 → private_key 조회
        String privateKey = walletApiClient.getPrivateKey(walletAddress);

        // 4. private_key 암호화 후 저장
        String encryptedPk = CryptoUtil.encrypt(privateKey);
        UserPrivateKey userPrivateKey = UserPrivateKey.builder()
                .wallet(wallet)
                .pkValue(encryptedPk)
                .build();
        userPrivateKeyRepository.save(userPrivateKey);

        return wallet;
    }
    @Transactional(readOnly = true)
    public Page<WalletTransaction> getTransactions(
            Long userId,
            TransactionTypeOption transactionType,
            TransactionPeriod period,
            TransactionSort sort,
            Pageable pageable
    ) {
        // 기간 처리
        LocalDateTime fromDate = switch (period) {
            case WEEK -> LocalDateTime.now().minusWeeks(1);
            case MONTH -> LocalDateTime.now().minusMonths(1);
            case YEAR -> LocalDateTime.now().minusYears(1);
            default -> null; // ALL
        };

        // 정렬 처리 (Pageable 덮어쓰기 가능)
        Sort sortOption = (sort == TransactionSort.OLDEST)
                ? Sort.by("createdAt").ascending()
                : Sort.by("createdAt").descending();

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sortOption
        );
// 쿼리 실행
        if (transactionType == TransactionTypeOption.ALL) {
            return (fromDate != null)
                    ? walletTransactionRepository.findByWallet_User_UserIdAndCreatedAfter(
                    userId, fromDate, sortedPageable)
                    : walletTransactionRepository.findByWallet_User_UserId(userId, sortedPageable);
        } else {
            TransactionType txType = (transactionType == TransactionTypeOption.CHARGE)
                    ? TransactionType.CHARGE : TransactionType.USE;

            return (fromDate != null)
                    ? walletTransactionRepository.findByWallet_User_UserIdAndTransactionTypeAndCreatedAfter(
                    userId, txType, fromDate, sortedPageable)
                    : walletTransactionRepository.findByWallet_User_UserIdAndTransactionType(
                    userId, txType, sortedPageable);
        }

    }
    /**
     * ✅ 구강검진 리워드 지급
     */
    @Transactional
    public WalletTransaction giveReward(Long userId, Long oralCheckId, OralCheckResultType resultType) {
        if (walletTransactionRepository.existsByRefIdAndRefType(oralCheckId, "ORAL_CHECK")) {
            throw new IllegalStateException("이미 해당 구강검진에 대한 리워드가 지급되었습니다.");
        }

        Wallet userWallet = getWalletByUserId(userId);

        AdminWallet adminWallet = adminWalletService.getActiveAdminWallet();
        String sender = adminWallet.getAddress();
        String senderPk = CryptoUtil.decrypt(adminWallet.getPrivateKey());
        String receiver = userWallet.getAddress();

        long reward = resultType.getReward();
        if (reward <= 0) throw new IllegalArgumentException("리워드 금액이 유효하지 않습니다.");

        // 외부 API 호출
        walletApiClient.transferToken(CONTRACT_ADDRESS, sender, senderPk, receiver, reward);

        // 거래내역 기록 후 반환
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(userWallet)
                .amount(reward)
                .transactionType(TransactionType.REWARD_CHECKUP)
                .description("구강검진 리워드 지급")
                .refId(oralCheckId)
                .refType("ORAL_CHECK")
                .build();

        return walletTransactionRepository.save(tx);
    }

}
