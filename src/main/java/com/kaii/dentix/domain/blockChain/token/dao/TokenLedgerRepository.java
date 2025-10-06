package com.kaii.dentix.domain.blockChain.token.dao;

import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenLedgerRepository extends JpaRepository<TokenLedger, Long> {
}