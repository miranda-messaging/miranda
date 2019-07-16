package com.ltsllc.miranda.operations.syncfiles.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.Message.Subjects.Timeout;

public class SyncFilesTimeoutMessage extends Message {
    public SyncFilesTimeoutMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super(Message.Subjects.Timeout, senderQueue, sender);
    }
}
