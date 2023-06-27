package com.kaii.dentix.common;

import org.springframework.restdocs.snippet.Attributes;

import static org.springframework.restdocs.snippet.Attributes.key;

public class DocumentOptionalGenerator {

    public static Attributes.Attribute setFormat(String value) {
        return key("format").value(value);
    }

    public static Attributes.Attribute userNumberFormat() {
        return setFormat("01012345678 (10자리 ~ 11자리)");
    }

    public static Attributes.Attribute yesNoFormat() {
        return setFormat("Y: Yes, N: No");
    }

    public static Attributes.Attribute userGenderFormat() {
        return setFormat("M: 남성, W: 여성");
    }

    public static Attributes.Attribute userBirthFormat() {
        return setFormat("yyyyMMdd");
    }

    public static Attributes.Attribute dateFormat() {
        return setFormat("yyyy-MM-dd");
    }

    public static Attributes.Attribute dateTimeFormat() {
        return setFormat("yyyy-MM-dd HH:mm:ss");
    }


}