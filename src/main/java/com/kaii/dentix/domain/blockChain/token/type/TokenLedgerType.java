package com.kaii.dentix.domain.blockChain.token.type;

public enum TokenLedgerType {
    ISSUE,       // 토큰 발행 (총 발급)
    CHARGE,      // 관리자 지갑 충전
    REWARD,      // 사용자 리워드 지급
    TRANSFER,    // 사용자 간 송금
    MANUAL,      // 관리자 수동 지급
    RETRIEVE,    // 회수
    USE          // 사용자 사용 (결제 등)
}