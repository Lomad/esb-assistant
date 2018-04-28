package com.winning.monitor.message;

/**
 * Created by nicholasyan on 16/9/5.
 */
public class MessagePackage {

    private MessageHead head;

    private Message message;

    public MessageHead getHead() {
        return head;
    }

    public void setHead(MessageHead head) {
        this.head = head;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
