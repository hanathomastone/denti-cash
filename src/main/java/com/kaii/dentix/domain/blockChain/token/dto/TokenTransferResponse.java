package com.kaii.dentix.domain.blockChain.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenTransferResponse {

    @JsonProperty("Date")
    private String date;

    @JsonProperty("Sender")
    private String sender;

    @JsonProperty("Receiver")
    private String receiver;

    @JsonProperty("Amount")
    private Long amount;
}