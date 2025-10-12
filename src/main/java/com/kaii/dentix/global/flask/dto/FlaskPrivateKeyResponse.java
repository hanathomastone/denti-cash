package com.kaii.dentix.global.flask.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlaskPrivateKeyResponse {
    @JsonProperty("private_key")
    private String private_key;   // Flask /acc/get_private_key 응답
}