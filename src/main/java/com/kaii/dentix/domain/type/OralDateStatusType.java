package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OralDateStatusType {

    HEALTHY(0, "일별 구강 상태값", "건강"),

    GOOD(1, "일별 구강 상태값", "양호"),

    ATTENTION(2, "일별 구강 상태값", "주의"),

    DANGER(3, "일별 구강 상태값", "위험"),
    QUESTIONNAIRE(4, "일별 구강 상태값", "문진표"),
    ORAL_CHECK_PERIOD(5, "일별 구강 상태값", "권장 촬영 기간"),
    TODAY(6, "일별 구강 상태값", "오늘");

    private final int id;
    private final String description;
    private final String value;
}
