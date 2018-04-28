package com.winning.monitor.superisor.consumer.api.message;

import com.winning.monitor.message.Message;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultMessageQueue<T extends Message>
        implements MessageQueue<T> {
    private BlockingQueue<T> m_queue;

    private AtomicInteger m_count = new AtomicInteger();

    public DefaultMessageQueue(int size) {
        m_queue = new LinkedBlockingQueue<T>(size);
    }

    @Override
    public boolean offer(T tree) {
        return m_queue.offer(tree);
    }


    @Override
    public T peek() {
        return m_queue.peek();
    }

    @Override
    public T poll() {
        try {
            return m_queue.poll(5, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return m_queue.size();
    }
}
