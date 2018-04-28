package com.winning.monitor.supervisor.core.period;


import com.winning.monitor.message.Message;
import com.winning.monitor.superisor.consumer.api.analysis.AbstractMessageAnalyzer;
import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzer;
import com.winning.monitor.superisor.consumer.api.message.MessageQueue;
import org.slf4j.Logger;

public class PeriodTask<T extends Message> implements Runnable {

    private MessageAnalyzer<T> m_analyzer;

    private MessageQueue<T> m_queue;

    private long m_startTime;

    private int m_queueOverflow;

    private Logger m_logger;

    private int m_index;

    public PeriodTask(MessageAnalyzer analyzer, MessageQueue queue, long startTime) {
        m_analyzer = analyzer;
        m_queue = queue;
        m_startTime = startTime;
    }

    public void setIndex(int index) {
        m_index = index;
    }

    public int getQueueSize() {
        return m_queue.size();
    }

    public boolean enqueue(T tree) {
        boolean result = m_queue.offer(tree);

        if (!result) { // trace queue overflow
            m_queueOverflow++;

//            if (m_queueOverflow % (10 * CatConstants.ERROR_COUNT) == 0) {
//                m_logger.warn(m_analyzer.getClass().getSimpleName() + " queue overflow number " + m_queueOverflow);
//            }
        }
        return result;
    }

    public void finish() {
        try {
            m_analyzer.doCheckpoint(true);
            m_analyzer.destroy();
        } catch (Exception e) {
//            Cat.logError(e);
        }
    }

    public MessageAnalyzer getAnalyzer() {
        return m_analyzer;
    }


    @Override
    public void run() {
        try {
            m_analyzer.analyze(m_queue);
        } catch (Exception e) {
//            Cat.logError(e);
        }
    }


    public void shutdown() {
        if (m_analyzer instanceof AbstractMessageAnalyzer) {
            ((AbstractMessageAnalyzer<?>) m_analyzer).shutdown();
        }
    }
}