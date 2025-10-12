package com.kaii.dentix.domain.blockChain.wallet.domain;

import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
@Entity
@Table(name = "user_wallet")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserWallet extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_wallet_id")
    private Long userWalletId;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, referencedColumnName = "userId")
    private User user;

    @Column(nullable = false, unique = true, length = 128)
    private String address;

    @Column(nullable = false, length = 512)
    private String privateKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = true, referencedColumnName = "contract_id")
    private TokenContract contract;

    @Column(nullable = false)
    private Long balance;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    public void prePersist() {
        if (balance == null) balance = 0L;
        if (!active) active = true;
    }

    public void addBalance(Long amount) {
        if (amount != null && amount > 0) {
            this.balance += amount;
        }
    }

    //토큰 차감
    public void subtractBalance(Long amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("차감할 금액은 0보다 커야 합니다.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
        this.balance -= amount;
    }

    //잔액 갱신용 (직접 설정)
    public void updateBalance(Long newBalance) {
        this.balance = newBalance;
    }
    public void updatePrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}