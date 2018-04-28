package com.winning.monitor.superisor.consumer.api.analysis;


import com.winning.monitor.message.Message;
import com.winning.monitor.superisor.consumer.api.message.MessageQueue;

public interface MessageAnalyzer<T extends Message> {

    void analyze(MessageQueue<T> queue);

    void destroy();

    void doCheckpoint(boolean atEnd);

    long getStartTime();

    void initialize(long startTime, long duration, long extraTime);

    int getAnanlyzerCount();

    void setIndex(int index);

}
