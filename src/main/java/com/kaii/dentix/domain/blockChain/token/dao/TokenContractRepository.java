package com.kaii.dentix.domain.blockChain.token.dao;


import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TokenContractRepository extends JpaRepository<TokenContract, Long> {

    // ✅ 현재 활성화된 컨트랙트 1개 조회
    @Query("SELECT c FROM TokenContract c WHERE c.active = true")
    Optional<TokenContract> findActiveContract();

    // ✅ 특정 contract_address 로 조회
    Optional<TokenContract> findByContractAddress(String contractAddress);
}