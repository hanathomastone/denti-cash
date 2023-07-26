package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentsType {

    CARD(1, "콘텐츠 타입", "카드뉴스"),

    VIDEO(2, "콘텐츠 타입", "동영상"),

    ANIMATION(3, "콘텐츠 타입", "애니메이션");

    private final int id;

    private final String description;

    private final String value;

}
