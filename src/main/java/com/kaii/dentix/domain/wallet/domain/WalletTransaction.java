package com.kaii.dentix.domain.wallet.domain;

import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "walletTransaction")
public class WalletTransaction extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walletId", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transactionType", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(nullable = false, length = 200)
    private String description;

    // ✅ 참조 관계 (예: CHECKUP, SURVEY 등)
    @Column(name = "refType", length = 50)
    private String refType;

    @Column(name = "refId")
    private Long refId;

    /**
     * ✅ 정적 생성 메서드 (Wallet에서 편하게 생성용)
     */
    public static WalletTransaction createSyncLog(Wallet wallet, Long amount, String description) {
        return WalletTransaction.builder()
                .wallet(wallet)
                .amount(amount)
                .transactionType(TransactionType.SYNC_BALANCE)
                .description(description != null ? description : "자동 잔액 동기화")
                .build();
    }
}
