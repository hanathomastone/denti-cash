package com.kaii.dentix.domain.blockChain.token.domain;

import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerSourceType;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerStatus;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import com.kaii.dentix.global.common.entity.TimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "token_ledger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenLedger extends TimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_ledger_id")
    private Long id;
    @Column(unique = true, nullable = true)
    private String txHash;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private TokenContract contract;

    @ManyToOne
    @JoinColumn(name = "sender_admin_wallet_id")
    private AdminWallet senderAdminWallet;

    @ManyToOne
    @JoinColumn(name = "sender_user_wallet_id")
    private UserWallet senderUserWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_admin_wallet_Id")
    private AdminWallet receiverAdminWallet;

    @ManyToOne
    @JoinColumn(name = "receiver_user_wallet_Id")
    private UserWallet receiverUserWallet;

    @Column(nullable = false, precision = 38, scale = 0)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenLedgerType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenLedgerStatus status;

    @Column(length = 255)
    private String message;

    @Column(length = 255)
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 20)
    private TokenLedgerSourceType sourceType;

    public void markSuccess(String message) {
        this.status = TokenLedgerStatus.SUCCESS;
        this.message = message;
    }

    public void markFailed(String message) {
        this.status = TokenLedgerStatus.FAILED;
        this.message = message;
    }
}