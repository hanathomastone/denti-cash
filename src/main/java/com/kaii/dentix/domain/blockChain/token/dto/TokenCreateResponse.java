package com.kaii.dentix.domain.blockChain.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenCreateResponse {
    @JsonProperty("contract address")
    private String contractAddress;
}