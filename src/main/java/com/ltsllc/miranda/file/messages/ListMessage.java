package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/8/2017.
 */
public class ListMessage extends Message {
    public ListMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.List, senderQueue, sender);
    }
}
