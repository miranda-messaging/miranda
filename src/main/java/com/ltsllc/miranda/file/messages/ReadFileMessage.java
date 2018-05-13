package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

public class ReadFileMessage extends Message {
    public ReadFileMessage (BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Message.Subjects.ReadFile, senderQueue, senderObject);
    }
}
