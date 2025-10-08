package com.kaii.dentix.domain.admin.dto.statistic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AdminTokenTransferRequest {
    @NotNull
    private Long userId;             // UI: 사용자 ID

    @NotNull @Min(1)
    private Long amount;             // UI: 토큰 지급 수

    @Size(max = 500)
    private String reason;           // UI: 발급 사유 (선택/저장)
}