package com.kaii.dentix.domain.blockChain.wallet.domain;

import com.kaii.dentix.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
@Entity
@Table(name = "userWallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userWalletId")
    private Long userWalletId;

    @OneToOne
    @JoinColumn(name = "userId", unique = true)
    private User user;

    @Column(nullable = false, unique = true, length = 128)
    private String walletAddress;

    @Column(nullable = false, length = 512)
    private String encryptedPrivateKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractId", nullable = false, referencedColumnName = "contractId")
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

    public void updateBalance(Long newBalance) {
        this.balance = newBalance;
    }

    public void addBalance(Long amount) {
        if (amount != null && amount > 0) {
            this.balance += amount;
        }
    }

    public void updateEncryptedPrivateKey(String encryptedPrivateKey) {
        this.encryptedPrivateKey = encryptedPrivateKey;
    }
}