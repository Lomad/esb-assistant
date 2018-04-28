package com.winning.monitor.data.storage.Utils;

import com.winning.esb.stable.MonitorConst;
import com.winning.monitor.data.api.transaction.vo.TransactionReportType;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.winning.monitor.utils.DateUtils.DAY_TYPE.CURRENT;
import static com.winning.monitor.utils.DateUtils.DAY_TYPE.TODAY;
import static com.winning.monitor.utils.DateUtils.*;

/**
 * Created by nicholasyan on 16/9/29.
 */
public class ConvertUtils {

    private static final String REALTIME_REPORT_COLL = "TransactionRealtimeReports";

    private static final String HOURLY_REPORT_COLL = "TransactionHourlyReports";

    private static final String DAILY_REPORT_COLL = "TransactionDailyReports";

    private static final String WEEKLY_REPORT_COLL = "TransactionWeeklyReports";

    private static final String MONTHLY_REPORT_COLL = "TransactionMonthlyReports";

    public static String getStringValue(Object value) {
        return value == null ? null : value.toString();
    }

    public static int getIntValue(Object value) {
        return value == null ? -1 : Integer.parseInt(value.toString());
    }

    public static Set<String> getStringSetValue(Object value) {
        if (value instanceof List) {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            for (Object s : ((List) value)) {
                set.add(getStringValue(s));
            }
        }
        return null;
    }

    public static String getTargetDateString(Object source){
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

        if (source instanceof Long){
            return simpleDateFormat.format(new Date((Long) source));
        }else {
            try {
                //转换秒之前的点号，例如：“2017-12-13 16:52:23.251”转为“2017-12-13 16:52:23:251”
                source = source.toString().replace(".", ":");
                //转为目标格式
                Date date = format.parse(source.toString());
                return simpleDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String GetCollectionName(String domain) {
        return "Messages-" + domain;
    }

    public static String GetReportCollectionName(TransactionReportType dateType) {
        switch (dateType) {
            case REALTIME:
                return REALTIME_REPORT_COLL;
            case HOUR_IN_TODAY:
                return REALTIME_REPORT_COLL;
            case TODAY:
                return REALTIME_REPORT_COLL;
            case HOURLY:
                return HOURLY_REPORT_COLL;
            case DAILY:
                return DAILY_REPORT_COLL;
            case WEEKLY:
                return WEEKLY_REPORT_COLL;
            case MONTHLY:
                return MONTHLY_REPORT_COLL;
        }
        return null;
    }

    //根据时间粒度获取TransactionReports的startTime
    public static String GetStartTimeWithTimeGral(TransactionReportType dateType, String priTime) {
        String startTime;
        switch (dateType) {
            case REALTIME:
                startTime = getStartTime(CURRENT);
                break;
            case HOUR_IN_TODAY:
                startTime = priTime;
                break;
            case TODAY:
                startTime = getStartTime(TODAY);
                break;
            case DAILY:
                startTime = priTime + " 00:00:00";
                break;
            case WEEKLY:
                startTime = (String) getWeeklyDay(priTime).get("Monday");
                break;
            case MONTHLY:
                startTime = getMonthFirstDay(priTime) + " 00:00:00";
                break;
            default:
                startTime = null;
        }
        return startTime;
    }

    public static String GetEndTimeWithTimeGral(TransactionReportType dateType, String priTime) {
        String endTime;
        switch (dateType) {
            case REALTIME:
                endTime = getEndTime(CURRENT);
                break;
            case HOUR_IN_TODAY:
                endTime = priTime.replace(priTime.substring(14, 19), "59:59");
                break;
            case TODAY:
                endTime = getEndTime(TODAY);
                break;
            case DAILY:
                endTime = priTime + " 23:59:59";
                break;
            case WEEKLY:
                endTime = (String) getWeeklyDay(priTime).get("Sunday");
                break;
            case MONTHLY:
                endTime = getMonthLastDay(priTime) + " 23:59:59";
                break;
            default:
                endTime = null;
        }
        return endTime;
    }

    public static long GetStartTimeLongWithTimeGral(TransactionReportType dateType, String priTime){
        return toDateTime(GetStartTimeWithTimeGral(dateType, priTime));
    }

    public static long GetEndTimeLongWithTimeGral(TransactionReportType dateType, String priTime){
        return toDateTime(GetEndTimeWithTimeGral(dateType, priTime)) + 999;
    }

    /**
     * 将关键字添加到查询条件中
     */
    public static void addKeywordToQuery(Criteria criteria, String keyWords) {
        if (!StringUtils.isEmpty(keyWords) && keyWords.indexOf("=") > 0) {
            int splitIndex = keyWords.indexOf("=");
            String columnKey = keyWords.substring(0, splitIndex).trim();
            String columnValue = keyWords.substring(splitIndex + 1);
            //如果值不为空，则可以查询
            if (!StringUtils.isEmpty(columnValue)) {
                //如果key为“mainId”或“MessageID”，则使用精确查询
                if (MonitorConst.KeyMainId.equals(columnKey) || MonitorConst.KeyMessageID.equals(columnKey)) {
                    criteria.and("messageTree.message.data." + columnKey).is(columnValue);
                } else {
                    criteria.and("messageTree.message.data." + columnKey).regex(columnValue, "ix");
                }
            }
        }
    }


}