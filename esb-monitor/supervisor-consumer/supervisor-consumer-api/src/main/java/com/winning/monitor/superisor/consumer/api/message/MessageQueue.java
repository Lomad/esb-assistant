package com.winning.monitor.superisor.consumer.api.message;

import com.winning.monitor.message.Message;

/**
 * Created by nicholasyan on 16/9/9.
 */
public interface MessageQueue<T extends Message> {

    boolean offer(T message);

    T peek();

    T poll();

    int size();

}
