package com.maker.util;



import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {
    private static final String DATE_PATTERN="yyyy-MM-dd";//转化格式
    private static final DateTimeFormatter DATE_FORMATTER= DateTimeFormatter.ofPattern(DATE_PATTERN);
    private static final ZoneId ZONE_ID=ZoneId.systemDefault();

    public static Date stringToDate(String date){
        LocalDate localDate=LocalDate.parse(date,DATE_FORMATTER);
        Instant instant=localDate.atStartOfDay().atZone(ZONE_ID).toInstant();
        return Date.from(instant);
    }
}
