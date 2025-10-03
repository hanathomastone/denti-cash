package com.kaii.dentix.domain.userPrivateKey.dao;

import com.kaii.dentix.domain.userPrivateKey.domain.UserPrivateKey;
import com.kaii.dentix.domain.wallet.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserPrivateKeyRepository extends JpaRepository<UserPrivateKey, Long> {
    Optional<UserPrivateKey> findByWallet(Wallet wallet);
}
