package com.kaii.dentix.domain.type.oral;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OralCheckResultType {

    // 구강 검사 결과 종합

    HEALTHY(0, "건강",
            "전체적으로 건강한 상태"),

    GOOD(1, "양호",
            "전체적으로 양호한 상태"),

    ATTENTION(2, "주의",
            "전체적으로 주의가 필요한 상태"),

    DANGER(3, "위험",
            "전체적으로 위험한 상태");

    private final int id;
    private final String type; // 유형
    private final String doctorComment; // Comment

}
