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

    public static Attributes.Attribute genderFormat() {
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

    public static Attributes.Attribute oralCheckResultTotalFormat() {
        return setFormat("HEALTHY : 건강, GOOD : 양호, ATTENTION : 주의, DANGER : 위험");
    }

    public static Attributes.Attribute oralCheckDivisionScoreFormat() {
        return setFormat("HEALTHY : 건강, GOOD : 양호, ATTENTION : 주의, DANGER : 위험");
    }

    public static Attributes.Attribute oralCheckDivisionCommentFormat() {
        return setFormat("HEALTHY : 모두 잘 닦인 경우, UR : 상악 우측, UL : 상악 좌측, DR : 하악 우측, DL : 하악 좌측");
    }

}