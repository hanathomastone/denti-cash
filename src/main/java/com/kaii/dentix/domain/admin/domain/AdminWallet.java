package com.kaii.dentix.domain.admin.domain;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 관리자 지갑 주소
    @Column(nullable = false, unique = true)
    private String address;

    // 암호화된 PrivateKey
    @Column(nullable = false, length = 512)
    private String privateKey;
    @Column(nullable = false)
    private Long balance;

    @PrePersist
    public void prePersist() {
        if (balance == null) {
            balance = 0L;
        }
    }

    //여러 개 생성 가능하더라도 하나만 활성화
    @Column(nullable = false)
    private boolean active;

    public void deactivate() {
        this.active = false;
    }

    // 잔액 추가 / 차감 메서드 (편의)
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
}