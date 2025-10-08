package com.kaii.dentix.domain.blockChain.token.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// Flask Private Key 응답
@Data
public class FlaskPrivateKeyResponse {
    @JsonProperty("private_key")
    private String privateKey;
}
