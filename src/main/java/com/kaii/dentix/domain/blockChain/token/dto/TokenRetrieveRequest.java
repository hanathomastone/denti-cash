package com.kaii.dentix.domain.blockChain.token.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TokenRetrieveRequest {

    @NotBlank(message = "보유자(holder) 주소는 필수입니다.")
    private String holder;

    @NotBlank(message = "수신자(receiver) 주소는 필수입니다.")
    private String receiver;

    @NotNull(message = "회수 금액은 필수입니다.")
    @Min(value = 1, message = "회수 금액은 1 이상이어야 합니다.")
    private Long amount;
}