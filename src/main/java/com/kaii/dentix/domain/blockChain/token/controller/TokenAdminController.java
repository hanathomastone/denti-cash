package com.kaii.dentix.domain.blockChain.token.controller;

import com.kaii.dentix.domain.blockChain.token.application.TokenContractService;
import com.kaii.dentix.domain.blockChain.token.domain.TokenContract;
import com.kaii.dentix.domain.blockChain.token.dto.TokenCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/token")
@RequiredArgsConstructor
public class TokenAdminController {

    private final TokenContractService tokenContractService;

    @PostMapping("/create")
    public TokenContract createToken(@RequestBody TokenCreateRequest request) {
        return tokenContractService.createToken(request);
    }
}