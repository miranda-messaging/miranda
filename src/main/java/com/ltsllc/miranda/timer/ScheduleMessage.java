package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;
/**
 * Created by Clark on 1/22/2017.
 */
public class ScheduleMessage extends Message {
    private Message message;

    public ScheduleMessage(Subjects subject, BlockingQueue<Message> sender, Object senderObject) {
        super(subject, sender, senderObject);

        this.message = new TimeoutMessage (getSender(), getSenderObject());
    }

    public ScheduleMessage (Subjects subject, BlockingQueue<Message> senderQueue, Object sender, Message message) {
        super(subject, senderQueue, sender);

        this.message = message;
    }


    public Message getMessage() {
        return message;
    }

}
