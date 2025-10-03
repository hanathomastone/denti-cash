package com.kaii.dentix.domain.wallet.dao;

import com.kaii.dentix.domain.user.domain.User;
import com.kaii.dentix.domain.wallet.domain.Wallet;
import com.kaii.dentix.domain.wallet.domain.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUser(User user);
}