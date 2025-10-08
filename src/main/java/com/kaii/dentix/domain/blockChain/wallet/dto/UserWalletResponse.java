package com.kaii.dentix.domain.blockChain.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserWalletResponse {
    private String address;
    private String created; // 생성일 or null

    public UserWalletResponse(String address) {
        this.address = address;
    }
}