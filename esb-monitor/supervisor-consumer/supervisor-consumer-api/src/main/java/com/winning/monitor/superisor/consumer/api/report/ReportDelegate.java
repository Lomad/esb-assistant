package com.winning.monitor.superisor.consumer.api.report;

import java.util.Map;

public interface ReportDelegate<T> {

    void afterLoad(Map<String, T> reports);

    void beforeSave(Map<String, T> reports);

    byte[] buildBinary(T report);

    T parseBinary(byte[] bytes);

    String buildXml(T report);

    String getDomain(T report);

    T makeReport(String domain, long startTime, long duration);

    T mergeReport(T old, T other);

    T parseXml(String xml) throws Exception;

    boolean createHourlyTask(T report);
}