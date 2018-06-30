package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.basicclasses.User;

import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ListUsersResponseMessage extends Message {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public ListUsersResponseMessage (BlockingQueue<Message> senderQueue, Object senderObject, List<User> users) {
        super(Subjects.ListUsersResponse, senderQueue, senderObject);
        setUsers(users);
    }
}
