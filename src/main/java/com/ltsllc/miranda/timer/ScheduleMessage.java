package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;
/**
 * Created by Clark on 1/22/2017.
 */
public class ScheduleMessage extends Message {
    private Message message;
    private BlockingQueue<Message> receiver;

    public ScheduleMessage (Subjects subject, BlockingQueue<Message> senderQueue, Object sender,
                            BlockingQueue<Message> receiver, Message message) {
        super(subject, senderQueue, sender);

        this.message = message;
        this.receiver = receiver;
    }


    public Message getMessage() {
        return message;
    }

    public BlockingQueue<Message> getReceiver() {
        return receiver;
    }
}
