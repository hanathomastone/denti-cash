package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeviceType {

    iOS(1, "디바이스 종류", "아이폰");

    private final int id;
    private final String description;
    private final String value;

}
