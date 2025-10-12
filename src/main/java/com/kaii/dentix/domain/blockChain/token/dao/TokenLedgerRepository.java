package com.kaii.dentix.domain.blockChain.token.dao;

import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import com.kaii.dentix.domain.blockChain.token.type.TokenLedgerType;
import com.kaii.dentix.domain.blockChain.wallet.domain.UserWallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenLedgerRepository extends JpaRepository<TokenLedger, Long> {

    @EntityGraph(attributePaths = {
            "senderUserWallet", "receiverUserWallet",
            "senderUserWallet.user", "receiverUserWallet.user",
            "senderAdminWallet", "receiverAdminWallet", "contract"
    })
    Page<TokenLedger> findAllByTypeInAndCreatedBetween(
            List<TokenLedgerType> types,
            Date start,
            Date end,
            Pageable pageable
    );

    /**
     * 타입만 필터
     * (예: ISSUE, CHARGE, RECLAIM 전체 중 일부 — 기간 조건 없음)
     */
    @EntityGraph(attributePaths = {
            "senderUserWallet", "receiverUserWallet",
            "senderUserWallet.user", "receiverUserWallet.user",
            "senderAdminWallet", "receiverAdminWallet", "contract"
    })
    Page<TokenLedger> findAllByTypeIn(
            List<TokenLedgerType> types,
            Pageable pageable
    );

    /**
     *거래주소 + 타입 + 기간
     * (예: 특정 contractAddress에서 CHARGE/RECLAIM/ISSUE 내역만 + 기간)
     */
    @EntityGraph(attributePaths = {
            "senderUserWallet", "receiverUserWallet",
            "senderUserWallet.user", "receiverUserWallet.user",
            "senderAdminWallet", "receiverAdminWallet", "contract"
    })
    Page<TokenLedger> findAllByContract_ContractAddressAndTypeInAndCreatedBetween(
            String contractAddress,
            List<TokenLedgerType> types,
            Date start,
            Date end,
            Pageable pageable
    );

    /**
     *거래주소 + 타입
     * (예: 특정 contractAddress의 ISSUE/CHARGE/RECLAIM 전체 내역)
     */
    @EntityGraph(attributePaths = {
            "senderUserWallet", "receiverUserWallet",
            "senderUserWallet.user", "receiverUserWallet.user",
            "senderAdminWallet", "receiverAdminWallet", "contract"
    })
    Page<TokenLedger> findAllByContract_ContractAddressAndTypeIn(
            String contractAddress,
            List<TokenLedgerType> types,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {
            "senderUserWallet", "receiverUserWallet",
            "senderUserWallet.user", "receiverUserWallet.user",
            "senderAdminWallet", "receiverAdminWallet", "contract"
    })
    Page<TokenLedger> findAllByContract_ContractAddress(String contractAddress, Pageable pageable);


    //전체 기간 (정렬 지원)
    List<TokenLedger> findByReceiverUserWalletAndType(
            UserWallet wallet,
            TokenLedgerType type,
            Sort sort
    );

    //특정 기간 이후 (정렬 지원)
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

    //특정 지갑의 여러 타입 거래 조회 (정렬 지원)
    List<TokenLedger> findBySenderUserWalletAndTypeIn(
            UserWallet wallet,
            List<TokenLedgerType> types,
            Sort sort
    );

    //특정 지갑의 여러 타입 거래 조회 + 날짜 필터
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

    @Query("SELECT SUM(l.amount) FROM TokenLedger l WHERE l.type = 'ISSUE'")
    Long sumAllIssuedTokens();

    @Query("SELECT SUM(w.balance) FROM AdminWallet w")
    Long sumAllBalances();
    List<TokenLedger> findAllByOrderByCreatedDesc();

    @Query("SELECT SUM(l.amount) FROM TokenLedger l WHERE l.contract.contractAddress = :contractAddress AND l.type = 'ISSUE'")
    Long sumIssuedByContract(@Param("contractAddress") String contractAddress);

    @Query("SELECT SUM(w.balance) FROM AdminWallet w WHERE w.contract.contractAddress = :contractAddress")
    Long sumBalanceByContract(@Param("contractAddress") String contractAddress);

    @Query("SELECT l FROM TokenLedger l WHERE l.contract.contractAddress = :contractAddress ORDER BY l.created DESC")
    List<TokenLedger> findAllByContractAddress(@Param("contractAddress") String contractAddress);
    @EntityGraph(attributePaths = {
            "senderUserWallet",
            "receiverUserWallet",
            "senderAdminWallet",
            "receiverAdminWallet"
    })
    Page<TokenLedger> findAllByOrderByCreatedDesc(Pageable pageable);

}
