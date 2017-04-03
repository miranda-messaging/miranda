package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class GetUsersFileMessage extends Message {
    public GetUsersFileMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetUsersFile, senderQueue, sender);
    }
}
