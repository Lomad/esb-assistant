package com.winning.monitor.supervisor.core.period;

import com.winning.monitor.superisor.consumer.api.analysis.MessageAnalyzerManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


public class PeriodManager implements Runnable {

    public static long EXTRATIME = 3 * 60 * 1000L;
    private PeriodStrategy m_strategy;
    private List<Period> m_periods = new ArrayList<Period>();
    private boolean m_active;
    private Thread thread;

    @Autowired
    private MessageAnalyzerManager analyzerManager;

    //    @Inject
//    private MessageAnalyzerManager m_analyzerManager;
//    @Inject
//    private ServerStatisticManager m_serverStateManager;
//    @Inject
//    private Logger m_logger;
//
    public PeriodManager(long duration) {
        m_strategy = new PeriodStrategy(duration, EXTRATIME, EXTRATIME);
        m_active = true;
    }

    public void init() {
        long startTime = m_strategy.next(System.currentTimeMillis());
        this.startPeriod(startTime);
        this.start();
    }

    public void start() {
        if (thread != null) {
            return;
        }

        this.thread = new Thread(this);
        this.thread.setDaemon(true);
        this.thread.start();
    }


    private void endPeriod(long startTime) {
        int len = m_periods.size();

        for (int i = 0; i < len; i++) {
            Period period = m_periods.get(i);

            if (period.isIn(startTime)) {
                period.finish();
                m_periods.remove(i);
                break;
            }
        }
    }

    public Period findPeriod(long timestamp) {
        for (Period period : m_periods) {
            if (period.isIn(timestamp)) {
                return period;
            }
        }

        return null;
    }


    @Override
    public void run() {
        while (m_active) {
            try {
                long now = System.currentTimeMillis();
                long value = m_strategy.next(now);

                if (value > 0) {
                    startPeriod(value);
                } else if (value < 0) {
                    // last period is over,make it asynchronous
                    //Threads.forGroup("cat").start(new EndTaskThread(-value));
                    this.endPeriod(-value);
                }
            } catch (Throwable e) {
                //Cat.logError(e);
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void shutdown() {
        m_active = false;
    }

    private void startPeriod(long startTime) {
        long endTime = startTime + m_strategy.getDuration();
        Period period = new Period(startTime, endTime, this.analyzerManager);
        m_periods.add(period);
        period.start();
    }


}