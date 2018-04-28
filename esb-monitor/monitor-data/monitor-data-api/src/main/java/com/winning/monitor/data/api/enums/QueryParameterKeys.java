package com.winning.monitor.data.api.enums;

/**
 * @Author Lemod
 * @Version 2017/6/2
 */
public enum QueryParameterKeys {

    DOMAIN("domain"), STARTTIME("startTime"), ENDTIME("endTime"), SOC("soc"),
    TIMETYPE("timeType"),SPECIFIEDHOUR("specifiedHour"),TIME("time"),
    STARTINDEX("startIndex"),PAGESIZE("pageSize"),TARGET("target");

        private String key;

        QueryParameterKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

}
