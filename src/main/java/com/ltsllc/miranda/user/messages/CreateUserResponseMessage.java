package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/11/2017.
 */
public class CreateUserResponseMessage extends Message {
    private Results result;
    private User user;
    private String additionalInfo;

    public User getUser() {
        return user;
    }

    public Results getResult() {
        return result;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public CreateUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, User user, Results result) {
        super(Subjects.CreateUserResponse, senderQueue, sender);

        this.user = user;
        this.result = result;
    }

    public CreateUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, User user, Results result,
                                      String additionalInfo)
    {
        super(Subjects.CreateUserResponse, senderQueue, sender);

        this.user = user;
        this.result = result;
        this.additionalInfo = additionalInfo;
    }
}
