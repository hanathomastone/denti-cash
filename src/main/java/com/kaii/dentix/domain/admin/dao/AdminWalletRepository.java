package com.kaii.dentix.domain.admin.dao;

import com.kaii.dentix.domain.admin.domain.AdminWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminWalletRepository extends JpaRepository<AdminWallet, Long> {
    Optional<AdminWallet> findByActiveTrue();
}
