package com.winning.monitor.supervisor.core.task;

import com.winning.monitor.agent.config.utils.NetworkInterfaceManager;
import com.winning.monitor.supervisor.core.task.exception.TaskStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by nicholasyan on 16/9/27.
 */
@Service
public class TaskManager {

    public static final int REPORT_HOUR = 0;
    public static final int REPORT_DAILY = 1;
    public static final int REPORT_WEEK = 2;
    public static final int REPORT_MONTH = 3;
    private static final long ONE_HOUR = 60 * 60 * 1000L;
    private static final long ONE_DAY = 24 * ONE_HOUR;
    private static final int STATUS_TODO = 1;
    private static final Logger logger = LoggerFactory.getLogger(TaskManager.class);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ITaskDao taskDao;

    private void createDailyTask(Date period, String group, String domain, String name) throws TaskStoreException {
        createTask(period, group, domain, name, REPORT_DAILY);
    }

    private void createHourlyTask(Date period, String group, String domain, String name) throws TaskStoreException {
        createTask(period, group, domain, name, REPORT_HOUR);
    }

    private void createMonthlyTask(Date period, String group, String domain, String name) throws TaskStoreException {
        createTask(period, group, domain, name, REPORT_MONTH);
    }

    protected void createTask(Date period, String group, String domain, String name, int reportType) throws TaskStoreException {

        Task task = new Task();

        task.setId(UUID.randomUUID().toString());
        task.setCreationDate(new Date());
        task.setProducer(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
        task.setReportDomain(domain);
        task.setReportGroup(group);
        task.setReportName(name);
        task.setReportPeriod(period);
        task.setStatus(STATUS_TODO);
        task.setTaskType(reportType);

        task.setBgsj(simpleDateFormat.format(period));
        task.setCjsj(simpleDateFormat.format(task.getCreationDate()));

        taskDao.upsert(task);
    }

    public boolean createTask(Date period, String group, String domain, String name, TaskCreationPolicy prolicy) {
        try {
            if (prolicy.shouldCreateHourlyTask()) {
                createHourlyTask(period, group, domain, name);
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(period);

            int hour = cal.get(Calendar.HOUR_OF_DAY);
            cal.add(Calendar.HOUR_OF_DAY, -hour);
            Date currentDay = cal.getTime();

            //收集并生成当天内的数据
            if (prolicy.shouldCreateDailyTask()) {
                createDailyTask(new Date(currentDay.getTime()), group, domain, name);
//                if (cal.get(Calendar.HOUR_OF_DAY) % 4 == 0) {
//                    createDailyTask(new Date(currentDay.getTime() - ONE_DAY), domain, name);
//                    createDailyTask(new Date(currentDay.getTime() - ONE_DAY * 2), domain, name);
//                    createDailyTask(new Date(currentDay.getTime() - ONE_DAY * 3), domain, name);
//                }
            }

            //收集本周以及上周
            if (prolicy.shouldCreateWeeklyTask()) {
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                //每周一生成上周所有
//                if (dayOfWeek == 1) {
                createWeeklyTask(new Date(currentDay.getTime() - (dayOfWeek - 2) * ONE_DAY), group, domain, name);
//                }
//                createWeeklyTask(new Date(currentDay.getTime() - (dayOfWeek - 1 + 7) * ONE_DAY), domain, name);

//                if (dayOfWeek == 7) {
//                  createWeeklyTask(new Date(currentDay.getTime() - 7 * ONE_DAY), domain, name);
//                }
            }
            //收集本月以及上月
            if (prolicy.shouldCreateMonthTask()) {
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                createMonthlyTask(new Date(currentDay.getTime() - (dayOfMonth - 1) * ONE_DAY), group, domain, name);
                if (dayOfMonth == 1) {
                    cal.add(Calendar.MONTH, -1);
                    createMonthlyTask(cal.getTime(), group, domain, name);
                }
//                createMonthlyTask(new Date(currentDay.getTime() - (dayOfMonth - 1) * ONE_DAY), domain, name);

//                if (dayOfMonth == 1) {
//                    cal.add(Calendar.MONTH, -1);
//                    createMonthlyTask(cal.getTime(), domain, name);
//                }
            }
            return true;
        } catch (TaskStoreException e) {
//            Cat.logError(e);
            // TODO: 16/9/27 记录错误日志
            return false;
        }
    }

    private void createWeeklyTask(Date period, String group, String domain, String name) throws TaskStoreException {
        createTask(period, group, domain, name, REPORT_WEEK);

    }

    public enum TaskProlicy implements TaskCreationPolicy {

        ALL {
            @Override
            public boolean shouldCreateDailyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateHourlyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateMonthTask() {
                return true;
            }

            @Override
            public boolean shouldCreateWeeklyTask() {
                return true;
            }
        },

        HOULY {
            @Override
            public boolean shouldCreateDailyTask() {
                return false;
            }

            @Override
            public boolean shouldCreateHourlyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateMonthTask() {
                return false;
            }

            @Override
            public boolean shouldCreateWeeklyTask() {
                return false;
            }
        },

        ALL_EXCLUED_HOURLY {
            @Override
            public boolean shouldCreateDailyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateHourlyTask() {
                return false;
            }

            @Override
            public boolean shouldCreateMonthTask() {
                return true;
            }

            @Override
            public boolean shouldCreateWeeklyTask() {
                return true;
            }
        },

        DAILY {
            @Override
            public boolean shouldCreateDailyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateHourlyTask() {
                return false;
            }

            @Override
            public boolean shouldCreateMonthTask() {
                return false;
            }

            @Override
            public boolean shouldCreateWeeklyTask() {
                return false;
            }
        },

        HOURLY_AND_DAILY {
            @Override
            public boolean shouldCreateDailyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateHourlyTask() {
                return true;
            }

            @Override
            public boolean shouldCreateMonthTask() {
                return false;
            }

            @Override
            public boolean shouldCreateWeeklyTask() {
                return false;
            }
        };
    }

    public static interface TaskCreationPolicy {

        boolean shouldCreateHourlyTask();

        boolean shouldCreateDailyTask();

        boolean shouldCreateWeeklyTask();

        boolean shouldCreateMonthTask();
    }

}
