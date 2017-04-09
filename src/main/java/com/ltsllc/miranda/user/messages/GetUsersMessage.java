package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/5/2017.
 */
public class GetUsersMessage extends Message {
    public GetUsersMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetUsers, senderQueue, sender);
    }
}
