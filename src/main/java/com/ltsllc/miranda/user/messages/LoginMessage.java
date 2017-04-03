package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.objects.LoginObject;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginMessage extends Message {
    private LoginObject loginObject;

    public LoginObject getLoginObject() {
        return loginObject;
    }

    public LoginMessage (BlockingQueue<Message> senderQueue, Object sender, LoginObject loginObject) {
        super(Subjects.Login, senderQueue, sender);

        this.loginObject = loginObject;
    }
}
