package com.kaii.dentix.domain.type.oral;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OralCheckDivisionScoreType {

    // 부위별 구강 상태 결과 유형

    HEALTHY(0, "건강"),

    GOOD(1, "양호"),

    ATTENTION(2, "주의"),

    DANGER(3, "위험");

    private final int id;
    private final String type; // 유형

}
