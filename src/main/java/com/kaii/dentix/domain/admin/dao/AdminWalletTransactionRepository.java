package com.kaii.dentix.domain.admin.dao;

import com.kaii.dentix.domain.admin.domain.AdminWalletTransaction;
import com.kaii.dentix.domain.admin.domain.AdminWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
public interface AdminWalletTransactionRepository extends JpaRepository<AdminWalletTransaction, Long> {
    List<AdminWalletTransaction> findByAdminWalletOrderByCreatedDesc(AdminWallet adminWallet);

    /** 발행/사용된 토큰의 contractAddress 목록(중복 제거) */
    @Query("select distinct t.contractAddress from AdminWalletTransaction t where t.contractAddress is not null")
    List<String> findDistinctContractAddresses();
}
