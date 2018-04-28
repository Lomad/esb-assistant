package com.winning.monitor.utils;

import com.winning.monitor.data.api.enums.DateType;
import com.winning.monitor.data.api.enums.Time_Type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * The <tt>DateUtils</tt> class provides four kinds of date or time,such as:
 * <ul>
 *     <li>String</li>
 *     <li>Date</li>
 *     <li>long</li>
 *     <li>Calendar</li>
 * </ul>
 */
public class DateUtils {
    public static final long SECOND = 1000L;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;

    private static final String[] WeekList = new String[]{"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天"};

    public enum DAY_TYPE {
        NOW, CURRENT, TODAY, YESTERDAY
    }

    public static Long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String getCurrentTimeString() {
        Long now = getCurrentTime();
        Date day = new Date(now);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(day);
    }

    public static Long toDateTime(String str) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return toDateTime(str, dateFormat);
    }

    public static Long toDateTime(String str, SimpleDateFormat format) {
        Long timestap = null;
        try {
            timestap = format.parse(str).getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timestap;
    }

    public static String toDateString(Long time) {
        return toDateString(time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static String toDateString(Long time, SimpleDateFormat format) {
        Date day = new Date();
        day.setTime(time);
        return format.format(day);
    }

    public static Long getStartTime(Long time, DateType type) {
        Calendar cal = Calendar.getInstance();
        Date day = new Date();
        Long timeStamp = null;
        day.setTime(time);
        cal.setTime(day);
        if (type.equals(DateType.HOUR) || type.equals(DateType.LAST1H)) {
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.DAY)) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.WEEK)) {
            int firstDayOfWeek = cal.getFirstDayOfWeek();
            int today = cal.get(Calendar.DAY_OF_WEEK);
            if (firstDayOfWeek == Calendar.SUNDAY) {
                today = today - 1;
                if (today == 0) {
                    today = 7;
                }
            }
            cal.add(Calendar.DAY_OF_MONTH, 1 - today);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.MONTH)) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.LAST24H)) {
            int today = cal.get(Calendar.DAY_OF_MONTH);
            cal.add(Calendar.DAY_OF_MONTH, 1 - today);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTimeInMillis();
        }
        return timeStamp;
    }

    public static Long getEndTime(Long time, DateType type) {
        Calendar cal = Calendar.getInstance();
        Date day = new Date();
        Long timeStamp = null;
        day.setTime(time);
        cal.setTime(day);
        if (type.equals(DateType.HOUR)) {
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.DAY)) {
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.WEEK)) {
            int firstDayOfWeek = cal.getFirstDayOfWeek();
            int today = cal.get(Calendar.DAY_OF_WEEK);
            if (firstDayOfWeek == Calendar.SUNDAY) {
                today = today - 1;
                if (today == 0) {
                    today = 7;
                }
            }
            cal.add(Calendar.DAY_OF_WEEK, 7 - today);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            timeStamp = cal.getTimeInMillis();
        } else if (type.equals(DateType.MONTH)) {
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTimeInMillis() - 1;
        }
        return timeStamp;
    }

    public static HashMap getWeeklyTime(Long time) {
        Calendar cal = Calendar.getInstance();
        Date day = new Date();
        HashMap<String, Long> pointTimes = new HashMap<>();
        day.setTime(time);
        cal.setTime(day);
        int today = cal.get(Calendar.DAY_OF_WEEK);
        Long startTime = null;
        Long endTime = null;
        cal.add(Calendar.DAY_OF_WEEK, 1 - today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startTime = cal.getTimeInMillis();
        endTime = startTime + DAY * 7 - 1;
        pointTimes.put("statTime", startTime);
        pointTimes.put("endTime", endTime);
        return pointTimes;
    }

    public static Date toDate(String key) {
        return new Date(toDateTime(key));
    }

    public static String calculate(Long time) {
        Double temp = Math.floor(time / SECOND);
        if (temp <= 60) {
            return temp.longValue() + "秒";
        } else if (temp <= 60 * 60) {
            Double min = Math.floor(temp / 60);
            Double sec = temp % 60;
            return min.longValue() + "分" + sec.longValue() + "秒";
        } else if (temp <= 60 * 60 * 24) {
            Double hour = Math.floor(temp / (60 * 60));
            temp = temp % (60 * 60);
            Double min = Math.floor(temp / 60);
            Double sec = temp % 60;
            return hour.longValue() + "时" + min.longValue() + "分" + sec.longValue() + "秒";
        } else {
            Double day = Math.floor(temp / (60 * 60 * 24));
            temp = temp % (60 * 60 * 24);
            Double hour = Math.floor(temp / (60 * 60));
            temp = temp % (60 * 60);
            Double min = Math.floor(temp / 60);
            Double sec = temp % 60;
            return day.longValue() + "天" + hour.longValue() + "时" + min.longValue() + "分" + sec.longValue() + "秒";
        }
    }

    public static Long calculateday(Long start, Long end) {
        Long day;
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

    public static int getWeeek(Long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        int firstDayOfWeek = cal.getFirstDayOfWeek();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        if (firstDayOfWeek == Calendar.SUNDAY) {
            today = today - 1;
            if (today == 0) {
                today = 7;
            }
        }
        return today;
    }

    public static String getWeekDay(int i) {
        if (i == 0) {
            i = 1;
        }
        return WeekList[i - 1];
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

    /*YQL date util*/
    public static long getStartTimeByLong(DAY_TYPE type, Date today) {
        Long timeStamp = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        if (type.equals(DAY_TYPE.NOW)) {
            timeStamp = today.getTime();
        } else if (type.equals(DAY_TYPE.CURRENT)) {
            timeStamp = cal.getTime().getTime();
            timeStamp = timeStamp - timeStamp % HOUR;
        } else if (type.equals(DAY_TYPE.TODAY) || type.equals(DAY_TYPE.YESTERDAY)) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timeStamp = cal.getTime().getTime();
            if (type.equals(DAY_TYPE.YESTERDAY)) {
                timeStamp = timeStamp - DAY;
            }
        }
        return timeStamp;
    }

    public static String getStartTime(DAY_TYPE type) {
        long timeStamp = System.currentTimeMillis();
        return getStartTime(type, new Date(timeStamp));
    }

    public static String getStartTime(DAY_TYPE type, Date today) {

        Long timeStamp = getStartTimeByLong(type, today);

        Date tempTime = new Date(timeStamp);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempTime);
    }

    public static long getEndTimeByLong(DAY_TYPE type, Date today) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Long timeStamp = cal.getTime().getTime();
        if (type.equals(DAY_TYPE.YESTERDAY)) {
            timeStamp = timeStamp - 1000L;
        } else if (type.equals(DAY_TYPE.TODAY)) {
            timeStamp = timeStamp + DAY - 1000L;
        }
        return timeStamp;
    }

    public static String getEndTime(DAY_TYPE type) {
        long timeStamp = System.currentTimeMillis();
        return getEndTime(type, new Date(timeStamp));
    }

    public static String getEndTime(DAY_TYPE type, Date today) {
        Date tempTime = null;

        if (type.equals(DAY_TYPE.CURRENT)) {
            Long timeStamp = getStartTimeByLong(type, today);
            timeStamp += 59 * MINUTE + 59 * SECOND;

            tempTime = new Date(timeStamp);
        } else {
            Long timeStamp = getEndTimeByLong(type, today);
            tempTime = new Date(timeStamp);
        }

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempTime);
    }

    public static HashMap getWeeklyDay(String selected) {
        HashMap<String, String> pointTimes = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormat.parse(selected));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int today = cal.get(Calendar.DAY_OF_WEEK);

        String startTime;
        String endTime;

        if (today == 1) {
            startTime = dateFormat.format(new Date(cal.getTimeInMillis() - 6 * DAY)) + " 00:00:00";
            endTime = dateFormat.format(new Date(cal.getTimeInMillis())) + " 23:59:59";
        } else {
            startTime = dateFormat.format(new Date(cal.getTimeInMillis() - (today - 2) * DAY)) + " 00:00:00";

            endTime = dateFormat.format(new Date(cal.getTimeInMillis() + (8 - today) * DAY)) + " 23:59:59";
        }

        pointTimes.put("Monday", startTime);
        pointTimes.put("Sunday", endTime);

        return pointTimes;
    }

    public static String getMonthFirstDay(String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            calendar.setTime(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMinimum(Calendar.DAY_OF_MONTH));

        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static String getMonthLastDay(String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            Date startTime = new SimpleDateFormat("yyyy-MM-dd").parse(time);
            calendar.setTime(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.set(Calendar.DAY_OF_MONTH, calendar
                .getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    public static Map<String, String> getPointsTimeByType(String type) {
        Map<String, String> pointsTime = new HashMap<>();

        String startTime, endTime;
        startTime = endTime = null;
        if (Time_Type.TODAY.getType().equalsIgnoreCase(type)) {
            startTime = getStartTime(DAY_TYPE.TODAY);
            endTime = getEndTime(DAY_TYPE.TODAY);
        } else if (Time_Type.YESTERDAY.getType().equalsIgnoreCase(type)) {
            startTime = getStartTime(DAY_TYPE.YESTERDAY);
            endTime = getEndTime(DAY_TYPE.YESTERDAY);
        } else if (Time_Type.LAST_WEEK.getType().equalsIgnoreCase(type)) {
            Long now = System.currentTimeMillis();
            now = now - DAY * 7;
            Date select = new Date(now);

            Map<String, String> pointTimes =
                    getWeeklyDay(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(select));
            startTime = pointTimes.get("Monday");
            endTime = pointTimes.get("Sunday");
        }

        pointsTime.put("startTime", startTime);
        pointsTime.put("endTime", endTime);
        return pointsTime;
    }

}
