package com.winning.monitor.supervisor.core.period;

/**
 * 时间间隔策略
 */
public class PeriodStrategy {

    private long m_duration;

    private long m_extraTime;

    private long m_aheadTime;

    private long m_lastStartTime;

    private long m_lastEndTime;

    /**
     * 创建一个时间间隔策略
     *
     * @param duration  时间间隔
     * @param extraTime 超过这个时间则过期
     * @param aheadTime 提前这个时间获取新的间隔开始时间
     */
    public PeriodStrategy(long duration, long extraTime, long aheadTime) {
        m_duration = duration;
        m_extraTime = extraTime;
        m_aheadTime = aheadTime;
        m_lastStartTime = -1;
        m_lastEndTime = 0;
    }

    public long getDuration() {
        return m_duration;
    }

    /**
     * Now对于的时间间隔的开始时间,如果结果<0,则说明已经过期,=0说明时间未到
     *
     * @param now 当前时间
     * @return
     */
    public long next(long now) {
        long startTime = now - now % m_duration;

        // for current period
        if (startTime > m_lastStartTime) {
            m_lastStartTime = startTime;
            return startTime;
        }

        // prepare next period ahead
        if (now - m_lastStartTime >= m_duration - m_aheadTime) {
            m_lastStartTime = startTime + m_duration;
            return startTime + m_duration;
        }

        // last period is over
        if (now - m_lastEndTime >= m_duration + m_extraTime) {
            long lastEndTime = m_lastEndTime;
            m_lastEndTime = startTime;
            return -lastEndTime;
        }

        return 0;
    }
}