package com.ltsllc.miranda.miranda.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A message that tells a subsystem to stop processing.
 */
public class StopMessage extends Message {
    public StopMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Stop, senderQueue, sender);
    }
}
