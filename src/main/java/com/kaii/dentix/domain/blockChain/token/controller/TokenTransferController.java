package com.kaii.dentix.domain.blockChain.token.controller;

import com.kaii.dentix.domain.blockChain.token.application.TokenTransferService;
import com.kaii.dentix.domain.blockChain.token.domain.TokenLedger;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/token")
@RequiredArgsConstructor
public class TokenTransferController {

    private final TokenTransferService tokenTransferService;

    @PostMapping("/transfer/{adminWalletId}/{userWalletId}/{amount}")
    public TokenLedger transfer(
            @PathVariable Long adminWalletId,
            @PathVariable Long userWalletId,
            @PathVariable Long amount
    ) {
        return tokenTransferService.transferToken(adminWalletId, userWalletId, amount);
    }
}