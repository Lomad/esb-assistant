package com.winning.monitor.agent.sender;

import com.winning.monitor.message.Message;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/6.
 */
public interface IDataEntityStorage<T extends Message> {

    void initialize();

    boolean put(T data);

    List<T> get(int size);

    int remainSize();

    void shutdown();

    int maxSize();
}
