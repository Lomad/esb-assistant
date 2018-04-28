package com.winning.monitor.superisor.consumer.api.report;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hourly report manager by domain of one report type(such as Transaction, Event, Problem, Heartbeat etc.) produced in
 * one machine for a couple of hours.
 */
public abstract class AbstractReportManager<T> implements ReportManager<T> {

    public static final long HOUR = 60 * 60 * 1000L;
    protected Map<Long, Map<String, T>> m_reports = new ConcurrentHashMap<Long, Map<String, T>>();
    private ConcurrentHashMap<String, String> m_valids = new ConcurrentHashMap<String, String>();


    @Override
    public void destory() {

    }

    @Override
    public void initialize() {

    }

    public void cleanup(long time) {
        List<Long> startTimes = new ArrayList<Long>(m_reports.keySet());

        for (long startTime : startTimes) {
            if (startTime < time) {
                m_reports.remove(startTime);
            }
        }
    }

    @Override
    public Set<String> getDomains(long startTime) {
        Map<String, T> reports = m_reports.get(startTime);

        if (reports == null) {
            return new HashSet<String>();
        } else {
            Set<String> domains = reports.keySet();
            Set<String> result = new HashSet<String>();

            for (String domain : domains) {
                if (validateDomain(domain)) {
                    result.add(domain);
                }
            }
            return result;
        }
    }

    @Override
    public T getHourlyReport(long startTime, String group, String domain, boolean createIfNotExist) {
        Map<String, T> reports = m_reports.get(startTime);

        String reportid = group + "." + domain;

        if (reports == null && createIfNotExist) {
            synchronized (m_reports) {
                reports = m_reports.get(startTime);

                if (reports == null) {
                    reports = new ConcurrentHashMap<String, T>();
                    m_reports.put(startTime, reports);
                }
            }
        }

        if (reports == null) {
            reports = new LinkedHashMap<String, T>();
        }

        T report = reports.get(reportid);

        if (report == null && createIfNotExist) {
            synchronized (reports) {
                report = this.makeReport(group, domain, startTime, HOUR);
                reports.put(reportid, report);
            }
        }

        if (report == null) {
            report = this.makeReport(group, domain, startTime, HOUR);
        }

        return report;
    }

    protected abstract T makeReport(String group, String domain, long startTime, long duration);

    @Override
    public Map<String, T> getHourlyReports(long startTime) {
        Map<String, T> reports = m_reports.get(startTime);

        if (reports == null) {
            return Collections.emptyMap();
        } else {
            return reports;
        }
    }


    private boolean validateDomain(String domain) {
        boolean result = true;

        if (!m_valids.contains(domain)) {
            int length = domain.length();
            char c;

            for (int i = 0; i < length; i++) {
                c = domain.charAt(i);

                if (c > 126 || c < 32) {
                    return false;
                }
            }
            m_valids.put(domain, domain);
        }
        return result;
    }

}
