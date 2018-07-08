package com.ltsllc.miranda.manager.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class FileWrittenMessage extends Message {
    public FileWrittenMessage (BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.FileWritten, senderQueue, senderObject);
    }
}
