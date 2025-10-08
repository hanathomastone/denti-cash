package com.kaii.dentix.domain.admin.dao;

import com.kaii.dentix.domain.admin.domain.AdminWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
