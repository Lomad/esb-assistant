package com.winning.monitor.data.storage.api.entity;

import com.winning.monitor.agent.logging.message.MessageTree;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicholasyan on 16/10/25.
 */
public class MessageTreeList {

    private List<MessageTree> messageTrees = new ArrayList<>();

    private long totalSize;

    public List<MessageTree> getMessageTrees() {
        return messageTrees;
    }

    public void setMessageTrees(List<MessageTree> messageTrees) {
        this.messageTrees = messageTrees;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public void addMessageTree(MessageTree messageTree) {
        this.messageTrees.add(messageTree);
    }
}
