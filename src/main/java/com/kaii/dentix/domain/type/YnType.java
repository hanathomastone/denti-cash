package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum YnType {

    Y(0, "여부 확인", "확인"),
    N(1, "여부 확인", "미 확인");

    private final int id;
    private final String description;
    private final String value;

}
