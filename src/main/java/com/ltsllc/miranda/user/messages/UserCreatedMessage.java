package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/7/2017.
 */
public class UserCreatedMessage extends Message {
    public UserCreatedMessage(BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.UserCreated, senderQueue, sender);
    }
}
