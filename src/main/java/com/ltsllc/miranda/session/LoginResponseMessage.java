package com.ltsllc.miranda.session;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/1/2017.
 */
public class LoginResponseMessage extends Message {
    private boolean loginSuccessful;
    private String additionalInfo;

    public boolean getLoginSuccessful() {
        return loginSuccessful;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public LoginResponseMessage (BlockingQueue<Message> senderQueue, Object sender, boolean loginSuccessful) {
        super(Subjects.LoginResponse, senderQueue, sender);

        this.loginSuccessful = loginSuccessful;
        this.additionalInfo = null;
    }

    public LoginResponseMessage (BlockingQueue<Message> senderQueue, Object sender, boolean loginSuccessful, String additionalInfo) {
        super(Subjects.LoginResponse, senderQueue, sender);

        this.loginSuccessful = loginSuccessful;
        this.additionalInfo = additionalInfo;
    }
}
