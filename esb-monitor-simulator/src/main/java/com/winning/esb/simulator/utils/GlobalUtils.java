package com.winning.esb.simulator.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static com.winning.esb.simulator.utils.GlobalConstant.MILLISFORMAT;

/**
 * @Author Lemod
 * @Version 2018/3/7
 */
public class GlobalUtils {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final DateTimeFormatter SECOND_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter MILLIS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public static final long HOUR = 1000L * 60 * 60;

    public static Date queryDateByLocalDate(LocalDate localDate) throws ParseException {
        String date = localDate.format(FORMATTER);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(date);
    }

    public static Long queryTimestampByLocalDate(LocalDate localDate) {
        try {
            return queryDateByLocalDate(localDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Long queryTimestampByLocalDate(LocalDate localDate, int hour, int minus) {
        if (hour == 24) {
            LocalTime localTime = LocalTime.of(0, minus);
            LocalDateTime dateTime = LocalDateTime.of(localDate.plusDays(1), localTime);
            return dateTime.toEpochSecond(ZoneOffset.of("+8")) * 1000;
        } else {
            LocalTime localTime = LocalTime.of(hour, minus);
            LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
            return dateTime.toEpochSecond(ZoneOffset.of("+8")) * 1000;
        }
    }

    public static String queryTimeByLocalDate(LocalDate localDate, int hour) {
        LocalTime localTime = LocalTime.of(hour, 0, 0);
        LocalDateTime dateTime = LocalDateTime.of(localDate, localTime);
        return dateTime.format(SECOND_FORMATTER);
    }

    public static Long queryTimestampByDateAndTime(LocalDate targetDate, long sampleTime) {
        Date date = new Date(sampleTime);
        LocalTime localTime = date.toInstant().atZone(ZONE_ID).toLocalTime();

        LocalDateTime targetDateTime = LocalDateTime.of(targetDate, localTime);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        try {
            return dateFormat.parse(targetDateTime.format(MILLIS_FORMATTER)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int GenerateRandomByThreshold(int cardinal, String threshold) {
        //根据阈值获取百分比
        int symbolIndex = threshold.indexOf("%");
        Double percent = new Double(threshold.substring(0, symbolIndex)) / 100;

        return new Double(Math.random() * (cardinal * percent)).intValue();
    }

    public static int getCurrentHour() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static Long getSampleTimestamp(String sampleDate, int targetHour, int minus) {
        LocalDate date = LocalDate.parse(sampleDate, FORMATTER);
        return queryTimestampByLocalDate(date, targetHour, minus);
    }

    public static Integer getTargetCount(long sampleCount, Float percent) {
        Float targetCount = sampleCount * percent;
        return targetCount.intValue();
    }

    public static String formatLongTime(long time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(MILLISFORMAT);
        return dateFormat.format(new Date(time));
    }
}
