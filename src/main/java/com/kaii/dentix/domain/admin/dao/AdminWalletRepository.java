package com.kaii.dentix.domain.admin.dao;

import com.kaii.dentix.domain.admin.domain.AdminWallet;
import com.kaii.dentix.domain.admin.dto.AdminWalletSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdminWalletRepository extends JpaRepository<AdminWallet, Long> {

    /**
     * 활성화된 관리자 지갑 조회
     * AdminWallet의 필드명이 'active'이므로 이렇게 수정
     */
    Optional<AdminWallet> findByActiveTrue();

    /**
     * 주소로 관리자 지갑 조회
     */
    Optional<AdminWallet> findByAddress(String address);

    List<AdminWallet> findAll();

    @Query("""
        SELECT new com.kaii.dentix.domain.admin.dto.AdminWalletSummaryDto(
            w.adminWalletId,
            w.address,
            COALESCE(SUM(CASE WHEN l.type = com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType.ISSUE THEN l.amount END), 0),
            COALESCE(SUM(CASE WHEN l.type = com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType.USE THEN l.amount END), 0),
            COALESCE(SUM(CASE WHEN l.type = com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType.ISSUE THEN l.amount END), 0)
            - COALESCE(SUM(CASE WHEN l.type = com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType.USE THEN l.amount END), 0)
        )
        FROM AdminWallet w
        LEFT JOIN TokenLedger l ON l.senderAdminWallet = w
        GROUP BY w.adminWalletId, w.address
    """)
    List<AdminWalletSummaryDto> findWalletSummaries();
    List<AdminWallet> findAllByActiveTrue();
}

