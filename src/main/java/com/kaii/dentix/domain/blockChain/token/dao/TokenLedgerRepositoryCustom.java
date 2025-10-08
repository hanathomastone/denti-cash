package com.kaii.dentix.domain.blockChain.token.dao;

import com.kaii.dentix.domain.blockChain.token.dto.AdminTokenLedgerListRequest;
import com.kaii.dentix.domain.blockChain.token.dto.TokenLedgerResponse;
import org.springframework.data.domain.Page;

public interface TokenLedgerRepositoryCustom {
    Page<TokenLedgerResponse> findAllWithFilter(AdminTokenLedgerListRequest request);
}