package com.kaii.dentix.domain.userPrivateKey.domain;

import com.kaii.dentix.domain.wallet.domain.Wallet;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_private_key")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrivateKey extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 암호화된 값 저장
    @Column(name = "pkValue", nullable = false, length = 512)
    private String pkValue;

    // ✅ Wallet 1:1
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "walletId", nullable = false, unique = true)
    private Wallet wallet;
}
