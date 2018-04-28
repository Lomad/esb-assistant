package com.winning.esb.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by SuperUser on 2017/7/27.
 */
public class DateUtils {
    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    private static final String[] WeekList = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};

    //时间格式
    private static final String FOMATERSTRING = "yyyy-MM-dd HH:mm:ss";
    private static final String FOMATERSTRING_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String FOMATERSTRING_YMD = "yyyy-MM-dd";
    private static final String FOMATERSTRING_HMS = "HH:mm:ss";

//    public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(FOMATERSTRING);
//    public static final SimpleDateFormat DATEFORMAT_SSS = new SimpleDateFormat(FOMATERSTRING_SSS);
//    public static final SimpleDateFormat DATEFORMAT_YMD = new SimpleDateFormat(FOMATERSTRING_YMD);
//    public static final SimpleDateFormat DATEFORMAT_HMS = new SimpleDateFormat(FOMATERSTRING_HMS);

    /**
     * 由于DateFormat是非线程安全的，所以需要使用ThreadLocal
     */
    public static final ThreadLocal<DateFormat> DATEFORMAT = new ThreadLocal<>();

    public static void setDateFormat() {
        setDateFormat(DATEFORMAT);
    }

    public static void setDateFormat(ThreadLocal<DateFormat> threadLocal) {
        if (threadLocal == null) {
            threadLocal = DATEFORMAT;
        }
        DateFormat df = threadLocal.get();
        if (df == null) {
            df = new SimpleDateFormat(FOMATERSTRING);
            threadLocal.set(df);
        }
    }

    public static final ThreadLocal<DateFormat> DATEFORMAT_SSS = new ThreadLocal<>();

    public static void setDateformatSss() {
        DateFormat df = DATEFORMAT_SSS.get();
        if (df == null) {
            df = new SimpleDateFormat(FOMATERSTRING_SSS);
            DATEFORMAT_SSS.set(df);
        }
    }

    public static final ThreadLocal<DateFormat> DATEFORMAT_YMD = new ThreadLocal<>();

    public static void setDateformatYmd() {
        DateFormat df = DATEFORMAT_YMD.get();
        if (df == null) {
            df = new SimpleDateFormat(FOMATERSTRING_YMD);
            DATEFORMAT_YMD.set(df);
        }
    }

    public static final ThreadLocal<DateFormat> DATEFORMAT_HMS = new ThreadLocal<>();

    public static void setDateformatHms() {
        DateFormat df = DATEFORMAT_HMS.get();
        if (df == null) {
            df = new SimpleDateFormat(FOMATERSTRING_HMS);
            DATEFORMAT_HMS.set(df);
        }
    }

    public static Long getCurrentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取当前日期时间的字符串(yyyy-MM-dd HH:mm:ss)
     */
    public static String getCurrentDatetimeString() {
        setDateFormat();
        return toDateString(getCurrentTime(), DATEFORMAT.get());
    }

    /**
     * 获取当前日期时间的字符串(yyyy-MM-dd HH:mm:ss.SSS)
     */
    public static String getCurrentDatetimeMiliSecondString() {
        setDateformatSss();
        return toDateString(getCurrentTime(), DATEFORMAT_SSS.get());
    }

    /**
     * 获取当前日期的字符串(yyyy-MM-dd)
     */
    public static String getCurrentDateString() {
        setDateformatYmd();
        return toDateString(getCurrentTime(), DATEFORMAT_YMD.get());
    }

    /**
     * 获取当前时间的字符串(HH:mm:ss)
     */
    public static String getCurrentTimeString() {
        setDateformatHms();
        return toDateString(getCurrentTime(), DATEFORMAT_HMS.get());
    }

    /**
     * 转为格式“yyyy-MM-dd HH:mm:ss”
     */
    public static String toDateString(Long time) {
        setDateFormat();
        return toDateString(time, DATEFORMAT.get());
    }
    /**
     * 转为格式“yyyy-MM-dd HH:mm:ss.SSS”
     */
    public static String toDateStringSss(Long time) {
        setDateformatSss();
        return toDateString(time, DATEFORMAT_SSS.get());
    }
    /**
     * 转为格式“yyyy-MM-dd”
     */
    public static String toDateStringYmd(Long time) {
        setDateformatYmd();
        return toDateString(time, DATEFORMAT_YMD.get());
    }
    /**
     * 转为格式“HH:mm:ss”
     */
    public static String toDateStringHms(Long time) {
        setDateformatHms();
        return toDateString(time, DATEFORMAT_HMS.get());
    }

    public static String toDateString(Long time, DateFormat format) {
        Date day = new Date();
        day.setTime(time);
        return format.format(day);
    }

    /**
     * 将“yyyy-MM-dd HH:mm:ss”格式字符串时间转为时间戳
     */
    public static Long toDateTime(String str) {
        setDateFormat();
        return toDateTime(str, DATEFORMAT.get());
    }

    /**
     * 将“yyyy-MM-dd HH:mm:ss.SSS”格式字符串时间转为时间戳
     */
    public static Long toDateTimeSSS(String str) {
        setDateformatSss();
        return toDateTime(str, DATEFORMAT_SSS.get());
    }

    public static Long toDateTime(String str, DateFormat format) {
        Long timestap = null;
        try {
            timestap = format.parse(str).getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timestap;
    }

    /**
     * 计算开始时间与结束时间相差的天数
     */
    public static Long calculateDay(Long start, Long end) {
        Long day = 1L;
        Double temp = Double.valueOf(Math.abs(end - start));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(start));
        int day1 = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTime(new Date(end));
        int day2 = cal.get(Calendar.DAY_OF_YEAR);
        if (temp < DAY) {
            if (day2 != day1) {
                day = 2L;
            } else {
                day = 1L;
            }
        } else {
            Double d = Math.ceil(temp / DAY);
            day = d.longValue() + 1;
        }
        return day;
    }

    /**
     * 获取输入时间与当前时间的时间差（毫秒）
     *
     * @param start 开始时间
     */
    public static int diffMilliSecond(Date start) {
        if (start == null) {
            return 0;
        }
        return diffMilliSecond(start, new Date());
    }

    /**
     * 获取时间差（毫秒）
     *
     * @param start 开始时间
     * @param end   结束时间
     */
    public static int diffMilliSecond(Date start, Date end) {
        if (start == null || end == null) {
            return 0;
        }
        return (int) (end.getTime() - start.getTime());
    }

    /**
     * 在基准时间上，加上相应的数量（秒数、小时数、天数……）
     *
     * @param baseDate    基准时间
     * @param dateType    添加类型（取值与“Calendar.MINUTE、Calendar.HOUR……”相同）
     * @param adds        添加数量（秒数、小时数、天数），如果为正数，表示加上，如果为负数，表示减去
     * @param clearToZero 是否重置低于dateType的时间为0，例如：如果datatype为“Calendar.HOUR”，且clearToZero为true，则需要将分钟、秒、毫秒设为0
     * @return 返回添加后的时间
     */
    public static Date addDate(Date baseDate, int dateType, int adds, boolean clearToZero) {
        if (baseDate == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.add(dateType, adds);
        if (clearToZero) {
            if (Calendar.MILLISECOND == dateType) {

            } else if (Calendar.SECOND == dateType) {
                cal.set(Calendar.MILLISECOND, 0);
            } else if (Calendar.MINUTE == dateType) {
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else if (Calendar.HOUR_OF_DAY == dateType) {
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else if (Calendar.DAY_OF_MONTH == dateType || Calendar.DAY_OF_WEEK == dateType) {
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            } else if (Calendar.MONTH == dateType) {
                cal.set(Calendar.DAY_OF_MONTH, 0);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
            }
        }
        Date result = cal.getTime();
        return result;
    }

    /**
     * 获取秒、小时、天等
     *
     * @param dateType 获取类型（取值与“Calendar.MINUTE、Calendar.HOUR……”相同）
     */
    public static int getDate(Date date, int dateType) {
        if (date == null) {
            return -1;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int result = cal.get(dateType);
        return result;
    }

    /**
     * 获取时间戳最小值
     */
    public static long getTimestampMin() {
        return 0;
    }
    /**
     * 获取时间戳最大值
     */
    public static long getTimestampMax() {
        return 9999999999999L;
    }

    public static void main(String[] args) {
//        System.out.print(DateUtils.calculate(1000L*60+1000L*3));
//        System.out.print(DateUtils.calculateDay(1501632000000L,1501726451766L));

    }

    /**
     * 多线程测试
     */
    public static void testThread() {
        for (int i = 0; i < 100; i++) {
            new TestSimpleDateFormatThreadSafe().start();
        }
    }

    public static class TestSimpleDateFormatThreadSafe extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    this.join(2000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                try {
                    System.out.println(getCurrentDatetimeString() + ": "
                            + this.getName() + ":" + toDateString(toDateTime("2013-05-24 06:02:20")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}