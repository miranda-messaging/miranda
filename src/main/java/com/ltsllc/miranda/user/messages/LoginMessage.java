package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.login.LoginObject;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginMessage extends Message {
    private String name;

    public String getName() {
        return name;
    }

    public LoginMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        super(Subjects.Login, senderQueue, sender);

        this.name = name;
    }
}
