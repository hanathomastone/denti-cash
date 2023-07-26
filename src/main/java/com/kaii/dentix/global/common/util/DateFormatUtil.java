package com.kaii.dentix.global.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtil {

    private static DateFormat format;

    public static String dateToString(String pattern, Date date) {
        format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date stringToDate(String pattern, String date) throws ParseException {
        format = new SimpleDateFormat(pattern);
        return format.parse(date);
    }

}
