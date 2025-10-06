package com.kaii.dentix.domain.admin.dao;

import com.kaii.dentix.domain.admin.domain.AdminWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdminWalletRepository extends JpaRepository<AdminWallet, Long> {
//    Optional<AdminWallet> findByActiveTrue();
    Optional<AdminWallet> findByAddress(String address);
    @Query("SELECT a FROM AdminWallet a WHERE a.active = true")
    Optional<AdminWallet> findActiveWallet();

}
