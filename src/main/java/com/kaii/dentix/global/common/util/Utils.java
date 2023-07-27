package com.kaii.dentix.global.common.util;

import org.springframework.stereotype.Component;

@Component
public class Utils {

    // 퍼센트 구하기
    public static float getPercent(int numerator, int denominator) {
        if (denominator == 0) return 0;
        float percent = ((float) numerator / denominator) * 100;

        return getDeleteDecimalValue(percent, 2);
    }

    // 소수점 자르기
    public static float getDeleteDecimalValue(float value, int count) {
        float m = (float) Math.pow(10.0, count);
        return Math.round(value * m) / m;
    }
}
