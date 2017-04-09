package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/7/2017.
 */
public class DuplicateUserMessage extends Message {
    public DuplicateUserMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.DuplicateUser, senderQueue, sender);
    }
}
