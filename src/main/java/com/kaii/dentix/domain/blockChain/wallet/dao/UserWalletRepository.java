package com.kaii.dentix.domain.blockChain.wallet.dao;

import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWalletRepository extends JpaRepository<UserWallet, Long> {
    Optional<UserWallet> findByUser_UserId(Long userId);
    Optional<UserWallet> findByWalletAddress(String walletAddress);
}