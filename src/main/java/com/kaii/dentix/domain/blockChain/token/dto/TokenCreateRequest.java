package com.kaii.dentix.domain.blockChain.token.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenCreateRequest {
    @JsonProperty("token_name")
    private String tokenName;
    @JsonProperty("token_symbol")
    private String tokenSymbol;
    private Long supply;
}