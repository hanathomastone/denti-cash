package com.kaii.dentix.domain.admin.domain;

import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "admin_wallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_wallet_id")
    private Long adminWalletId;

    @Column(nullable = false, unique = true, length = 128)
    private String address;

    @Column(nullable = false, length = 512)
    private String privateKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private TokenContract contract;

    @Column(nullable = false)
    private Long balance = 0L;

    @Column(nullable = false)
    private boolean active;


    // ✅ 토큰 증가
    public void addBalance(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("추가할 금액은 0보다 커야 합니다.");
        }
        this.balance = (this.balance == null ? 0L : this.balance) + amount;
    }

    // ✅ 토큰 차감
    public void subtractBalance(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("차감할 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    // ✅ 잔액 갱신용 (직접 설정)
    public void updateBalance(Long newBalance) {
        this.balance = newBalance;
    }

    /**
     * 지갑 비활성화
     */
    public void deactivate() {
        this.active = false;
    }

    /**
     * 지갑 활성화
     */
    public void activate() {
        this.active = true;
    }
}