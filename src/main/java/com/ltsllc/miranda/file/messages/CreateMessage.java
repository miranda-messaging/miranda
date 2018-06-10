package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

public class CreateMessage extends Message {
    public CreateMessage (BlockingQueue senderQueue, Object sender) {
        super(Subjects.Create, senderQueue, sender);
    }
}
