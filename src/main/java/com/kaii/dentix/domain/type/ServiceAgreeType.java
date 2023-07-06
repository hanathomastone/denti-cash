package com.kaii.dentix.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceAgreeType {

    ALL(1, "서비스 동의", "전체 동의"),
    TERMS(2, "서비스 동의", "서비스 이용약관 동의"),
    PRIVACY(3, "서비스 동의", "개인정보 수집 및 이용동의"),
    MARKETING(4, "서비스 동의", "마케팅 정보 수신 동의");

    private final int id;
    private final String description;
    private final String value;

}
