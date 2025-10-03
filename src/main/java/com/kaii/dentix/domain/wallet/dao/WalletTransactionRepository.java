package com.kaii.dentix.domain.wallet.dao;
import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.domain.wallet.domain.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    Page<WalletTransaction> findByWallet_User_UserId(Long userId, Pageable pageable);

    Page<WalletTransaction> findByWallet_User_UserIdAndCreatedAfter(Long userId, LocalDateTime fromDate, Pageable pageable);

    Page<WalletTransaction> findByWallet_User_UserIdAndTransactionType(Long userId, TransactionType transactionType, Pageable pageable);

    Page<WalletTransaction> findByWallet_User_UserIdAndTransactionTypeAndCreatedAfter(
            Long userId,
            TransactionType transactionType,
            LocalDateTime fromDate,
            Pageable pageable
    );
    boolean existsByWallet_IdAndTransactionTypeAndRefTypeAndRefId(
            Long walletId,
            TransactionType transactionType,
            String refType,
            Long refId
    );

    boolean existsByRefIdAndRefType(Long refId, String refType);



}