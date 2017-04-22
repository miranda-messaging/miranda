package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.user.User;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/2/2017.
 */
public class GetUserResponseMessage extends Message {
    private String name;
    private User user;
    private Results result;

    public User getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public Results getResult() {
        return result;
    }

    public GetUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String name, Results result, User user) {
        super(Subjects.GetUserResponse, senderQueue, sender);

        this.result = result;
        this.name = name;
        this.user = user;
    }
}
