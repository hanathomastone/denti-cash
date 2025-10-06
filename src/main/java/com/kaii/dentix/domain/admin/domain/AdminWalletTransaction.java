package com.kaii.dentix.domain.admin.domain;

import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "adminWalletTransaction")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminWalletTransaction extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adminWalletId", nullable = false)
    private AdminWallet adminWallet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TransactionType transactionType; // CHARGE, USE, etc

    @Column(nullable = false)
    private Long amount; // 거래량 (supply 등)

    @Column(length = 255)
    private String description; // 설명 (예: "토큰 생성")

    @Column(name = "contractAddress", length = 255)
    private String contractAddress;
}