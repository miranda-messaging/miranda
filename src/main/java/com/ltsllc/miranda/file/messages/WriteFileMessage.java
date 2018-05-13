package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A request to write the data of a file
 */
public class WriteFileMessage extends Message {
    public WriteFileMessage (BlockingQueue<Message> sender, Object senderObject) {
        super(Subjects.WriteFile, sender, senderObject);
    }
}
