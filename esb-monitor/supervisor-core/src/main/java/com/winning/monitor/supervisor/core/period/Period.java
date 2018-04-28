package com.winning.monitor.supervisor.core.period;


import com.winning.monitor.message.Message;
import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzer;
import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzerManager;
import com.winning.monitor.superisor.consumer.api.message.DefaultMessageQueue;
import com.winning.monitor.superisor.consumer.api.message.MessageQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

public class Period {
    private static int QUEUE_SIZE = 30000;
    private long m_startTime;
    private long m_endTime;
    private Map<String, List<PeriodTask>> m_tasks;

    private MessageAnalyzerManager m_analyzerManager;

    private Logger m_logger = LoggerFactory.getLogger(Period.class);

    public Period(long startTime, long endTime, MessageAnalyzerManager analyzerManager) {
        m_startTime = startTime;
        m_endTime = endTime;
        m_analyzerManager = analyzerManager;

        List<String> names = m_analyzerManager.getAnalyzerNames();

        m_tasks = new HashMap<String, List<PeriodTask>>();

        for (String name : names) {
            //获取指定时间内的分析器集合
            List<MessageAnalyzer> messageAnalyzers = m_analyzerManager.getAnalyzer(name, startTime);

            //初始化分析器处理任务
            for (MessageAnalyzer analyzer : messageAnalyzers) {
                //初始化消息队列
                MessageQueue queue = new DefaultMessageQueue(QUEUE_SIZE);
                //初始化分析任务
                PeriodTask task = new PeriodTask(analyzer, queue, startTime);

                //task.enableLogging(m_logger);
                //初始化<分析器,任务>哈希表,一个分析器可能存在多个处理任务
                List<PeriodTask> analyzerTasks = m_tasks.get(name);

                if (analyzerTasks == null) {
                    analyzerTasks = new ArrayList<PeriodTask>();
                    m_tasks.put(name, analyzerTasks);
                }
                analyzerTasks.add(task);
            }
        }
    }

    public boolean distribute(Message message) {
        // m_serverStateManager.addMessageTotal(tree.getDomain(), 1);
        boolean success = true;
        String ipAddress = message.getIpAddress();

        //遍历所有的任务集合
        for (Map.Entry<String, List<PeriodTask>> entry : m_tasks.entrySet()) {
            List<PeriodTask> tasks = entry.getValue();
            int length = tasks.size();
            int index = 0;
            boolean manyTasks = length > 1;

            //如果有多个任务存在,根据哈希值计算对于的处理线程
            if (manyTasks) {
                index = Math.abs(ipAddress.hashCode()) % length;
            }
            PeriodTask task = tasks.get(index);
            //加入处理队列
            boolean enqueue = task.enqueue(message);

            //如果失败,且存在多个处理任务,加入其它任务
            if (enqueue == false) {
                if (manyTasks) {
                    task = tasks.get((index + 1) % length);
                    enqueue = task.enqueue(message);

                    if (enqueue == false) {
                        success = false;
                    }
                } else {
                    success = false;
                }
            }
        }

        if (!success) {
            // m_serverStateManager.addMessageTotalLoss(tree.getDomain(), 1);
        }
        return success;
    }

    public void finish() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = new Date(m_startTime);
        Date endDate = new Date(m_endTime - 1);

        m_logger.info(String.format("Finishing %s tasks in period [%s, %s]", m_tasks.size(), df.format(startDate),
                df.format(endDate)));

        try {
            for (Map.Entry<String, List<PeriodTask>> tasks : m_tasks.entrySet()) {
                for (PeriodTask task : tasks.getValue()) {
                    task.finish();
                }
            }
        } catch (Throwable e) {
            m_logger.error("发生错误", e);
        } finally {
            m_logger.info(String.format("Finished %s tasks in period [%s, %s]", m_tasks.size(), df.format(startDate),
                    df.format(endDate)));
        }
    }

    public List<MessageAnalyzer> getAnalyzer(String name) {
        List<MessageAnalyzer> analyzers = new ArrayList<MessageAnalyzer>();
        List<PeriodTask> tasks = m_tasks.get(name);

        if (tasks != null) {
            for (PeriodTask task : tasks) {
                analyzers.add(task.getAnalyzer());
            }
        }
        return analyzers;
    }

    public List<MessageAnalyzer> getAnalzyers() {
        List<MessageAnalyzer> analyzers = new ArrayList<MessageAnalyzer>(m_tasks.size());

        for (Map.Entry<String, List<PeriodTask>> tasks : m_tasks.entrySet()) {
            for (PeriodTask task : tasks.getValue()) {
                analyzers.add(task.getAnalyzer());
            }
        }

        return analyzers;
    }

    public long getStartTime() {
        return m_startTime;
    }

    public boolean isIn(long timestamp) {
        return timestamp >= m_startTime && timestamp < m_endTime;
    }

    /**
     * 开始执行时间段处理任务
     */
    public void start() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        m_logger.info(String.format("Starting %s tasks in period [%s, %s]", m_tasks.size(),
                df.format(new Date(m_startTime)), df.format(new Date(m_endTime - 1))));

        //遍历所有的任务
        for (Map.Entry<String, List<PeriodTask>> tasks : m_tasks.entrySet()) {
            List<PeriodTask> taskList = tasks.getValue();

            for (int i = 0; i < taskList.size(); i++) {
                PeriodTask task = taskList.get(i);
                task.setIndex(i);

                //开启线程开始执行任务
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
                //Threads.forGroup("Cat-RealtimeConsumer").start(task);
            }
        }
    }

    public void showTaskQueueSize() {
        for (Map.Entry<String, List<PeriodTask>> tasks : m_tasks.entrySet()) {
            List<PeriodTask> taskList = tasks.getValue();
            //System.out.println("队列长度:" + taskList.get(0).getQueueSize());
        }
    }

}
