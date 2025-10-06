package com.kaii.dentix.domain.wallet.dao;
import com.kaii.dentix.domain.type.TransactionType;
import com.kaii.dentix.domain.wallet.domain.WalletTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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


    @Query("""
        SELECT t
        FROM WalletTransaction t
        JOIN FETCH t.wallet w
        JOIN FETCH w.user u
        WHERE (:userId IS NULL OR u.userId = :userId)
          AND (:type IS NULL OR t.transactionType = :type)
          AND (:start IS NULL OR t.created >= :start)
          AND (:end IS NULL OR t.created <= :end)
        ORDER BY t.created DESC
    """)
    Page<WalletTransaction> findTransactions(
            @Param("userId") Long userId,
            @Param("type") String type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            Pageable pageable
    );
}