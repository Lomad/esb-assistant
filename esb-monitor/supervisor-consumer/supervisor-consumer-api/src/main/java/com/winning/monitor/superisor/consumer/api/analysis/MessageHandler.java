package com.winning.monitor.superisor.consumer.api.analysis;

/**
 * Created by nicholasyan on 16/9/9.
 */
public interface MessageHandler<T> {

    String getMessageTypeName();

    String handleMessage(T message);

}