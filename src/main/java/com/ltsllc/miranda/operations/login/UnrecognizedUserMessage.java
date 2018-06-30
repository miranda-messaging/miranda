package com.ltsllc.miranda.operations.login;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.message.Message.Subjects.UnrecognizedUser;

public class UnrecognizedUserMessage extends Message {
    private String user;

    public UnrecognizedUserMessage (String user, BlockingQueue<Message> senderQueue, Object sender) {
        super(UnrecognizedUser, senderQueue, sender);
        setUser(user);
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
