package com.kaii.dentix.domain.blockChain.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Flask 토큰 생성 요청
@Data
public class FlaskTokenCreateRequest {
    @JsonProperty("token_name")
    private String tokenName;

    @JsonProperty("token_symbol")
    private String tokenSymbol;

    private Long supply;
}