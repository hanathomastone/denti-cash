package com.kaii.dentix.domain.blockChain.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenTransferRequest {

    @JsonProperty("contract_address")
    private String contractAddress;

    @JsonProperty("sender")
    private String sender;

    @JsonProperty("sender_private_key")
    private String senderPrivateKey;

    @JsonProperty("receiver")
    private String receiver;

    @JsonProperty("amount")
    private Long amount;
}