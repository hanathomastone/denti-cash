package com.kaii.dentix.domain.blockChain.token.domain;

import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 블록체인 토큰 거래 내역 (리워드, 송금, 충전 등)
 */
@Entity
@Table(name = "tokenLedger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenLedger extends TimeEntity {

    @Id
    @Column(name = "tokenLedgerId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 어떤 토큰 컨트랙트에서 발생한 거래인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractId", nullable = false)
    private TokenContract contract;

    // ✅ 송신자 (관리자 지갑 기준)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fromAdminWalletId")
    private AdminWallet fromAdminWallet;

    // ✅ 수신자 (사용자 지갑 기준)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "toUserWalletId")
    private UserWallet toUserWallet;

    // ✅ 트랜잭션 해시 (Flask로부터 받은 고유 txHash)
    @Column(nullable = false, unique = true, length = 128)
    private String txHash;

    // ✅ 송금 금액
    @Column(nullable = false, precision = 38, scale = 0)
    private BigDecimal amount;

    // ✅ 거래 유형 (예: REWARD, CHARGE, TRANSFER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TokenLedgerType type;

    // ✅ 거래 상태 (예: PENDING, SUCCESS, FAILED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TokenLedgerStatus status;

    // ✅ 에러 메시지나 트랜잭션 메모
    @Column(length = 255)
    private String message;

    // ✅ Flask 트랜잭션 수행 후 상태 업데이트용 메서드
    public void markSuccess(String message) {
        this.status = TokenLedgerStatus.SUCCESS;
        this.message = message;
    }

    public void markFailed(String message) {
        this.status = TokenLedgerStatus.FAILED;
        this.message = message;
    }
}
