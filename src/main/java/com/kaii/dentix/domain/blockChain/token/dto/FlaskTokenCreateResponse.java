package com.kaii.dentix.domain.blockChain.token.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Flask 토큰 생성 응답
@Data
public class FlaskTokenCreateResponse {
    @JsonProperty("contract_address")
    private String contractAddress;
}
