package com.kaii.dentix.domain.blockChain.token.dto;


import lombok.Getter;

@Getter
public class ManualTokenIssueRequest {
    private Long userId;      // 사용자 ID
    private Long amount;      // 토큰 수
    private String reason;    // 발급 사유
}
