package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GenderType {

    M(1, "성별", "남"),
    W(2, "성별", "여");

    private final int id;

    private final String description;

    private final String value;

}