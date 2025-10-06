package com.kaii.dentix.domain.admin.domain;

import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "adminWallet")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminWallet extends TimeEntity {

    @Id
    @Column(name = "adminWalletId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 관리자 지갑 주소 (on-chain address)
    @Column(nullable = false, unique = true, length = 128)
    private String address;

    // ✅ 암호화된 Private Key (복호화 금지, Flask로만 전달)
    @Column(nullable = false, length = 512)
    private String encryptedPrivateKey;

    // ✅ 연동된 토큰 컨트랙트 (어떤 ERC20 토큰인지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractId", nullable = false, referencedColumnName = "contractId")
    private TokenContract contract;

    // ✅ on-chain 잔액 (Flask 동기화 시 갱신)
    @Column(nullable = false)
    private Long balance;

    // ✅ 여러 개 중 하나만 활성화 가능
    @Column(nullable = false)
    private boolean active;

    // ---------- Hooks ----------
    @PrePersist
    public void prePersist() {
        if (balance == null) balance = 0L;
        if (!active) active = false;
    }

    // ---------- 비즈니스 로직 ----------
    public void deactivate() {
        this.active = false;
    }

    public void addBalance(Long amount) {
        if (amount != null && amount > 0) {
            this.balance += amount;
        }
    }

    public void subtractBalance(Long amount) {
        if (amount != null && amount > 0 && this.balance >= amount) {
            this.balance -= amount;
        }
    }

    // ✅ balance 외부 동기화용 (Flask → Spring)
    public void updateBalance(Long onChainBalance) {
        this.balance = onChainBalance;
    }
}
