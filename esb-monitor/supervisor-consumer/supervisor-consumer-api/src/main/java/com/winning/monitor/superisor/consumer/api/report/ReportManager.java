package com.winning.monitor.superisor.consumer.api.report;

import java.util.Map;
import java.util.Set;

public interface ReportManager<T> {

    void destory();

    void initialize();

    Set<String> getDomains(long startTime);

    T getHourlyReport(long startTime, String group, String domain, boolean createIfNotExist);

    Map<String, T> getHourlyReports(long startTime);

    Map<String, T> loadHourlyReports(long startTime, int index);

    void storeHourlyReports(long startTime, StoragePolicy storagePolicy, int index);

    static enum StoragePolicy {
        FILE,

        FILE_AND_DB;

        public boolean forDatabase() {
            return this == FILE_AND_DB;
        }

        public boolean forFile() {
            return this == FILE_AND_DB || this == FILE;
        }
    }
}
