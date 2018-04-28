package com.winning.monitor.supervisor.core.task;

import java.util.Date;

public interface TaskBuilder {

    String getName();

    boolean buildDailyTask(String name, String group, String domain, Date period);

    boolean buildHourlyTask(String name, String group, String domain, Date period);

    boolean buildMonthlyTask(String name, String group, String domain, Date period);

    boolean buildWeeklyTask(String name, String group, String domain, Date period);

}
