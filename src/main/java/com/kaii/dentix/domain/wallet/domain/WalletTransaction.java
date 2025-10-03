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

    // ✅ 중복 방지용
    @Column(name = "refType", length = 50)
    private String refType;

    @Column(name = "refId")
    private Long refId;
}
