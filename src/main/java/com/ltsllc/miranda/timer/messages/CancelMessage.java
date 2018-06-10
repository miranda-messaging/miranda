package com.ltsllc.miranda.timer.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Stop a schedule
 */
public class CancelMessage extends Message {
    private BlockingQueue<Message> receiver;

    public BlockingQueue<Message> getReceiver() {
        return receiver;
    }

    public void setReceiver(BlockingQueue<Message> receiver) {
        this.receiver = receiver;
    }

    public CancelMessage (BlockingQueue<Message> senderQueue, Object senderObject, BlockingQueue<Message> receiver) {
        super(Subjects.Cancel, senderQueue, senderObject);
        setReceiver(receiver);
    }
}
