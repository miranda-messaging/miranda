package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class Notification extends Message {
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Notification (BlockingQueue<Message> senderQueue, Object sender, Object data) {
        super(Subjects.Notification, senderQueue, sender);

        this.data = data;
    }

    public Notification (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Notification, senderQueue, sender);
    }

    public Notification (Subjects subject, BlockingQueue<Message> senderQueue, Object sender) {
        super(subject, senderQueue, sender);
    }

    public Notification (Subjects subject, BlockingQueue<Message> senderQueue, Object sender, Object data) {
        super(subject, senderQueue, sender);

        this.data = data;
    }
}
