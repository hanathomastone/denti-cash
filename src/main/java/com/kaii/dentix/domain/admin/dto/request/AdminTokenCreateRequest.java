package com.kaii.dentix.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
public class AdminTokenCreateRequest {
    @NotBlank(message = "토큰 이름은 필수입니다")
    @JsonProperty("token_name")       // ✅ 추가
    private String tokenName;

    @NotBlank(message = "토큰 심볼은 필수입니다")
    @JsonProperty("token_symbol")     // ✅ 추가
    private String tokenSymbol;

    @NotNull(message = "총 발행량은 필수입니다")
    @JsonProperty("supply")           // ✅ 추가
    private Long supply;
}