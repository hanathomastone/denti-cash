package com.kaii.dentix.domain.blockChain.token.type;

public enum TokenLedgerType {
    REWARD,      // 사용자 리워드 지급
    CHARGE,      // 관리자 지갑 충전
    TRANSFER,
    MANUAL// 일반 송금
}