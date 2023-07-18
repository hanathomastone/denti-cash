package com.kaii.dentix.domain.type.oral;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OralCheckDivisionCommentType {

    // 부위별 구강 상태 Comment

    HEALTHY(0, "모두 잘 닦인 경우",
            "전체적으로 양치를 잘하셨어요!"),

    UR(1, "상악 우측이 잘 안 닦인 경우",
            "양치질을 할 때, 상악 우측을 조금 더 신경 써 주세요."),

    UL(2, "상악 좌측이 잘 안 닦인 경우",
            "양치질을 할 때, 상악 좌측을 조금 더 신경 써 주세요."),

    DR(3, "하악 우측이 잘 안 닦인 경우",
            "양치질을 할 때, 하악 우측을 조금 더 신경 써 주세요."),

    DL(4, "하악 좌측이 잘 안 닦인 경우",
            "양치질을 할 때, 하악 우측을 조금 더 신경 써 주세요.");

    private final int id;
    private final String type; // 유형
    private final String summaryComment; // Comment

}
