package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/10/2017.
 */
public class DeleteUserMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.DeleteUser, senderQueue, sender);

        this.name = name;
    }
}
