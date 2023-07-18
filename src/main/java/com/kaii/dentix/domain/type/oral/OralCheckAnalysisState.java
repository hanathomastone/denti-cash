package com.kaii.dentix.domain.type.oral;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OralCheckAnalysisState {

    SUCCESS(1, "플라그 검사 분석 상태", "분석 완료"),
    FAIL(2, "플라그 검사 분석 상태", "분석 실패");

    private final int id;
    private final String title;
    private final String description;

}
