package com.kaii.dentix.domain.blockChain.token.dao;

import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenLedgerRepository extends JpaRepository<TokenLedger, Long> {
    // ✅ 전체 기간 (정렬 지원)
    List<TokenLedger> findByReceiverUserWalletAndType(
            UserWallet wallet,
            TokenLedgerType type,
            Sort sort
    );

    // ✅ 특정 기간 이후 (정렬 지원)
    List<TokenLedger> findByReceiverUserWalletAndTypeAndCreatedAfter(
            UserWallet wallet,
            TokenLedgerType type,
            Date fromDate,
            Sort sort
    );
    @Query("""
        SELECT tl
        FROM TokenLedger tl
        WHERE tl.senderUserWallet.address = :address
           OR tl.receiverUserWallet.address = :address
           OR tl.senderAdminWallet.address = :address
           OR tl.receiverAdminWallet.address = :address
    """)
    Page<TokenLedger> findByWalletAddress(@Param("address") String address, Pageable pageable);

    boolean existsByReceiverUserWalletAndTypeAndMessage(UserWallet receiverUserWallet, TokenLedgerType type, String message);

    List<TokenLedger> findByReceiverUserWallet(UserWallet wallet);
    List<TokenLedger> findBySenderUserWallet(UserWallet wallet);

    @Query("""
        SELECT tl FROM TokenLedger tl
        WHERE tl.senderUserWallet = :userWallet
           OR tl.receiverUserWallet = :userWallet
        ORDER BY tl.created DESC
    """)
    List<TokenLedger> findAllByUserWallet(@Param("userWallet") UserWallet userWallet);

    @Query("""
        SELECT tl FROM TokenLedger tl
        WHERE tl.senderUserWallet = :userWallet
           OR tl.receiverUserWallet = :userWallet
        ORDER BY tl.created DESC
    """)
    Page<TokenLedger> findAllByUserWallet(@Param("userWallet") UserWallet userWallet, Pageable pageable);

    List<TokenLedger> findAllByReceiverUserWalletOrSenderUserWallet(UserWallet receiver, UserWallet sender);

    @Query("SELECT t FROM TokenLedger t " +
            "WHERE t.receiverUserWallet.user.userId = :userId " +
            "AND (:fromDate IS NULL OR t.created >= :fromDate) " +
            "ORDER BY t.created DESC")
    List<TokenLedger> findAllLedgers(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDateTime fromDate
    );

    @Query("SELECT t FROM TokenLedger t " +
            "WHERE t.receiverUserWallet.user.userId = :userId " +
            "AND t.type = 'REWARD' " +
            "AND (:fromDate IS NULL OR t.created >= :fromDate) " +
            "ORDER BY t.created DESC")
    List<TokenLedger> findRewardLedgers(
            @Param("userId") Long userId,
            @Param("fromDate") LocalDateTime fromDate
    );


        // ✅ 특정 지갑의 여러 타입 거래 조회 (정렬 지원)
        List<TokenLedger> findBySenderUserWalletAndTypeIn(
                UserWallet wallet,
                List<TokenLedgerType> types,
                Sort sort
        );

        // ✅ 특정 지갑의 여러 타입 거래 조회 + 날짜 필터
        List<TokenLedger> findBySenderUserWalletAndTypeInAndCreatedAfter(
                UserWallet wallet,
                List<TokenLedgerType> types,
                Date fromDate,
                Sort sort
        );
    @Query("SELECT SUM(t.amount) FROM TokenLedger t WHERE t.type = :type")
    Optional<BigDecimal> sumAmountByType(TokenLedgerType type);



    @Query("""
    SELECT l FROM TokenLedger l
    WHERE (:type IS NULL OR l.type = :type)
      AND (:fromDate IS NULL OR l.created >= :fromDate)
    ORDER BY l.created DESC
""")
    List<TokenLedger> findAllByFilter(
            @Param("type") TokenLedgerType type,
            @Param("fromDate") Date fromDate
    );

        List<TokenLedger> findAllByContractAndType(TokenContract contract, TokenLedgerType type);

}
