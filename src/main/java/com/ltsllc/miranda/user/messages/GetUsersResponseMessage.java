package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.user.User;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/5/2017.
 */
public class GetUsersResponseMessage extends Message {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public GetUsersResponseMessage(BlockingQueue<Message> senderQueue, Object sender, List<User> users) {
        super(Subjects.GetUsersResponse, senderQueue, sender);

        this.users = users;
    }
}
