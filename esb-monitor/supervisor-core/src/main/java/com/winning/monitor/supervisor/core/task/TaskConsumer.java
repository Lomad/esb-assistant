package com.winning.monitor.supervisor.core.task;

import com.winning.monitor.agent.config.utils.NetworkInterfaceManager;
import com.winning.monitor.supervisor.core.task.exception.TaskStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.locks.LockSupport;

/**
 * Created by nicholasyan on 16/9/27.
 */
public class TaskConsumer implements Runnable {

    public static final int STATUS_TODO = 1;
    public static final int STATUS_DOING = 2;
    public static final int STATUS_DONE = 3;
    public static final int STATUS_FAIL = 4;

    private static final Logger logger = LoggerFactory.getLogger(TaskConsumer.class);
    private static final int MAX_TODO_RETRY_TIMES = 1;
    private final Thread thread;
    private volatile boolean running = true;
    private volatile boolean stopped = false;

    @Autowired
    private ITaskDao taskDao;

    @Autowired
    private ReportFacade reportFacade;

    public TaskConsumer() {
        this.thread = new Thread(this);
        this.thread.setDaemon(false);
    }

    public boolean checkTime() {
        Calendar cal = Calendar.getInstance();
        int minute = cal.get(Calendar.MINUTE);
        //当前时间分钟数如果大于3,则返回true
        if (minute > 3) {
            return true;
        } else {
            return false;
        }
    }

    @PostConstruct
    public void start() {
        this.running = true;
        if (this.thread.isInterrupted() || !this.thread.isAlive()) {
            this.thread.start();
        }
    }

    @Override
    public void run() {
        String localIp = getLoaclIp();
        while (running) {
            try {
                if (checkTime()) {
                    Task task = findDoingTask(localIp);
                    if (task == null) {
                        task = findTodoTask();
                    }
                    boolean again = false;
                    if (task != null) {
                        try {
                            task.setConsumer(localIp);
                            if (task.getStatus() == TaskConsumer.STATUS_DOING || updateTodoToDoing(task)) {
                                int retryTimes = 0;
                                while (!processTask(task)) {
                                    retryTimes++;
                                    if (retryTimes < MAX_TODO_RETRY_TIMES) {
                                        taskRetryDuration();
                                    } else {
                                        updateDoingToFailure(task);
                                        again = true;
                                        break;
                                    }
                                }
                                if (!again) {
                                    updateDoingToDone(task);
                                }
                            }
                        } catch (Throwable e) {
                            //Cat.logError(task.toString(), e);
                        }
                    } else {
                        taskNotFoundDuration();
                    }
                } else {
                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        // Ignore
                    }
                }
            } catch (Throwable e) {
                logger.error("报表任务轮询时发生错误", e);
            }
        }
        this.stopped = true;
    }

    protected Task findDoingTask(String ip) {
        Task task = null;
        try {
            task = taskDao.findByStatusConsumer(STATUS_DOING, ip);
        } catch (TaskStoreException e) {
        }
        return task;
    }

    protected Task findTodoTask() {
        Task task = null;
        try {
            task = taskDao.findByStatusConsumer(STATUS_TODO, null);
        } catch (TaskStoreException e) {
        }
        return task;
    }

    protected boolean updateDoingToDone(Task doing) {
        doing.setStatus(STATUS_DONE);
        doing.setEndDate(new Date());

        try {
            return taskDao.updateDoingToDone(doing) == 1;
        } catch (TaskStoreException e) {
            // TODO: 16/9/27 记录错误
        }
        return true;
    }

    protected boolean updateTodoToDoing(Task todo) {
        todo.setStatus(STATUS_DOING);
        todo.setConsumer(NetworkInterfaceManager.INSTANCE.getLocalHostAddress());
        todo.setStartDate(new Date());

        try {
            return taskDao.updateTodoToDoing(todo) == 1;
        } catch (TaskStoreException e) {
            // TODO: 16/9/27 记录错误
        }
        return true;
    }

    protected boolean updateDoingToFailure(Task doing) {
        doing.setStatus(STATUS_FAIL);
        doing.setEndDate(new Date());

        try {
            return taskDao.updateDoingToFail(doing) == 1;
        } catch (TaskStoreException e) {
            // TODO: 16/9/27 记录错误
            return false;
        }
    }

    protected boolean processTask(Task doing) {
        boolean result = false;
//        Transaction t = Cat.newTransaction("Task", doing.getReportName());
//        t.addData(doing.toString());
        try {
            result = reportFacade.builderReport(doing);
//            t.setStatus(Transaction.SUCCESS);
        } catch (Exception e) {
//            Cat.logError(e);
//            t.setStatus(e);
        } finally {
//            t.complete();
        }
        return result;
    }

    protected String getLoaclIp() {
        return NetworkInterfaceManager.INSTANCE.getLocalHostAddress();
    }

    protected void taskRetryDuration() {
        LockSupport.parkNanos(2L * 1000 * 1000 * 1000);// sleep 10 sec
    }

    protected void taskNotFoundDuration() {
        try {
            Thread.sleep(1 * 60 * 1000);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    @PreDestroy
    public void stop() {
        this.running = false;
    }
}
