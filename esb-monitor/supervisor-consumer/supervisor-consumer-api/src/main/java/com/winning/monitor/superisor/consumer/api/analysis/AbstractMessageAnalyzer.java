package com.winning.monitor.superisor.consumer.api.analysis;


import com.winning.monitor.message.Message;
import com.winning.monitor.superisor.consumer.api.message.MessageQueue;
import org.slf4j.Logger;

public abstract class AbstractMessageAnalyzer<T extends Message>
        implements MessageAnalyzer<T> {

    public static final long MINUTE = 60 * 1000L;

    public static final long ONE_HOUR = 60 * 60 * 1000L;

    public static final long ONE_DAY = 24 * ONE_HOUR;

    //	@Inject
//	protected ServerConfigManager m_serverConfigManager;
    protected long m_startTime;
    protected long m_duration;
    protected Logger m_logger;
    protected int m_index;
    private long m_extraTime;
    private long m_errors = 0;
    private volatile boolean m_active = true;

    @Override
    public void analyze(MessageQueue<T> queue) {
        while (!isTimeout() && isActive()) {
            T message = queue.poll();

            if (message != null) {
                try {
                    process(message);
                } catch (Throwable e) {
                    m_errors++;

                    if (m_errors == 1 || m_errors % 10000 == 0) {
                        //Cat.logError(e);
                    }
                }
            }
        }

        while (true) {
            T message = queue.poll();

            if (message != null) {
                try {
                    process(message);
                } catch (Throwable e) {
                    m_errors++;

                    if (m_errors == 1 || m_errors % 10000 == 0) {
//                        Cat.logError(e);
                    }
                }
            } else {
                break;
            }
        }
    }

    @Override
    public void destroy() {
//        super.release(this);
//        ReportManager<?> manager = this.getReportManager();
//
//        if (manager != null) {
//            manager.destory();
//        }
    }

    @Override
    public abstract void doCheckpoint(boolean atEnd);

    @Override
    public int getAnanlyzerCount() {
        return 1;
    }

    protected long getExtraTime() {
        return m_extraTime;
    }

//    public abstract R getReport(String domain);

    @Override
    public long getStartTime() {
        return m_startTime;
    }

    @Override
    public void initialize(long startTime, long duration, long extraTime) {
        m_extraTime = extraTime;
        m_startTime = startTime;
        m_duration = duration;

        loadReports();
    }

    protected boolean isActive() {
        synchronized (this) {
            return m_active;
        }
    }

    protected boolean isLocalMode() {
        return true;
        //return m_serverConfigManager.isLocalMode();
    }

    protected boolean isTimeout() {
        long currentTime = System.currentTimeMillis();
        long endTime = m_startTime + m_duration + m_extraTime;

        return currentTime > endTime;
    }

    protected abstract void loadReports();

    protected abstract void process(T message);

    public void shutdown() {
        synchronized (this) {
            m_active = false;
        }
    }

    @Override
    public void setIndex(int index) {
        m_index = index;
    }

}
