package com.kaii.dentix.domain.type.oral;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OralCheckDivisionCommentType {

    // 부위별 구강 상태 Comment

    UR(1, "상악 우측이 잘 안 닦인 경우",
            "상악우측"),

    UL(2, "상악 좌측이 잘 안 닦인 경우",
            "상악좌측"),

    DR(3, "하악 우측이 잘 안 닦인 경우",
            "하악우측"),

    DL(4, "하악 좌측이 잘 안 닦인 경우",
            "하악좌측");

    private final int id;
    private final String type; // 유형
    private final String summaryComment; // Comment

}
