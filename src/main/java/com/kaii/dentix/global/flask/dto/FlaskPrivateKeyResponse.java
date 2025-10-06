package com.kaii.dentix.global.flask.dto;

import lombok.Data;

@Data
public class FlaskPrivateKeyResponse {
    private String private_key;   // Flask /acc/get_private_key 응답
}