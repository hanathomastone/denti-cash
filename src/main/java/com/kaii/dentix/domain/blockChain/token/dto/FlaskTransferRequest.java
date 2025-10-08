package com.kaii.dentix.domain.blockChain.token.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlaskTransferRequest {
    private String contract_address;
    private String sender;
    private String sender_private_key;
    private String receiver;
    private Long amount;
}