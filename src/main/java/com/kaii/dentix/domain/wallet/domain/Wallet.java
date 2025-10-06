package com.kaii.dentix.domain.wallet.domain;

import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.userPrivateKey.domain.UserPrivateKey;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "wallet")
public class Wallet extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address", nullable = false, unique = true, length = 255)
    private String address;

    // 사용자 1:1 지갑
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private Long balance; // 보유 토큰

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WalletTransaction> transactions = new ArrayList<>();

    @OneToOne(mappedBy = "wallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserPrivateKey privateKey;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        this.balance = (this.balance == null) ? 0L : this.balance;
    }

    // ✅ 충전
    public WalletTransaction charge(Long amount, String description) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        this.balance += amount;

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(this)
                .amount(amount)
                .transactionType(TransactionType.CHARGE)
                .description(description)
                .build();

        this.transactions.add(tx);
        return tx;
    }

    // ✅ 사용
    public WalletTransaction use(Long amount, String description) {
        if (amount == null || amount <= 0) throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        if (this.balance < amount) throw new IllegalStateException("잔액 부족");

        this.balance -= amount;

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(this)
                .amount(amount)
                .transactionType(TransactionType.USE)
                .description(description)
                .build();

        this.transactions.add(tx);
        return tx;
    }

    // ✅ 구강검진 리워드
    public WalletTransaction rewardCheckup(long amount, String description, Long checkupId) {
        if (amount <= 0) throw new IllegalArgumentException("리워드 금액은 0보다 커야 합니다.");
        this.balance += amount;

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(this)
                .amount(amount)
                .transactionType(TransactionType.REWARD_CHECKUP)
                .description(description)
                .refType("CHECKUP")
                .refId(checkupId)
                .build();

        this.transactions.add(tx);
        return tx;
    }

    // ✅ 외부 동기화 or 관리 작업 시 자동 로그 생성
    public void updateBalance(Long newBalance, String description) {
        if (newBalance == null) return;

        long diff = newBalance - this.balance;
        if (diff == 0) return; // 변경 없음

        TransactionType type = TransactionType.SYNC_BALANCE; // balance 동기화용 타입

        WalletTransaction tx = WalletTransaction.builder()
                .wallet(this)
                .amount(Math.abs(diff))
                .transactionType(type)
                .description(description != null ? description : "자동 잔액 동기화")
                .build();

        this.transactions.add(tx);
        this.balance = newBalance;
    }
}
