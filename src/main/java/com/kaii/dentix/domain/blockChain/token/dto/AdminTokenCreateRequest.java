package com.kaii.dentix.domain.blockChain.token.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 토큰 컨트랙트 생성 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminTokenCreateRequest {

    @NotBlank(message = "토큰 이름은 필수입니다")
    private String tokenName;

    @NotBlank(message = "토큰 심볼은 필수입니다")
    private String tokenSymbol;

    @NotNull(message = "발행량은 필수입니다")
    @Min(value = 1, message = "최소 1개 이상이어야 합니다")
    private Long supply;
}