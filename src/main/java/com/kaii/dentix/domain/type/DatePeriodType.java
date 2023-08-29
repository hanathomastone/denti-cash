package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DatePeriodType {

    TODAY (1, "기간 종류", "오늘"),

    WEEK1 (2, "기간 종류", "1주일"),

    MONTH1 (3, "기간 종류", "1개월"),

    MONTH3 (4, "기간 종류", "3개월"),

    YEAR1 (6, "기간 종류", "1년"),

    ALL (7, "기간 종류", "전체");

    private final int id;

    private final String description;

    private final String value;

}
