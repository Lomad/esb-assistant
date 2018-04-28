package com.winning.monitor.agent.logging.storage;

import com.winning.monitor.agent.logging.message.MessageTree;
import com.winning.monitor.agent.sender.IDataEntityStorage;

import java.util.List;

/**
 * Created by nicholasyan on 16/9/8.
 */
public class MessageTreeStorage extends AbstractDataStorage<MessageTree>
        implements IDataEntityStorage<MessageTree> {

    public MessageTreeStorage(int maxQueueSize) {
        super(maxQueueSize);
    }

    @Override
    public void initialize() {

    }

    @Override
    public boolean put(MessageTree tree) {
        return super.offer(tree);
    }

    @Override
    public List get(int size) {
        return this.poll(size);
    }

    @Override
    public int remainSize() {
        return super.dataEntityQueue.size();
    }


}
