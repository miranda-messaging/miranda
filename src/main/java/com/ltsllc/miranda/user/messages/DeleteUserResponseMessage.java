package com.ltsllc.miranda.user.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/11/2017.
 */
public class DeleteUserResponseMessage extends Message {
    private String name;
    private Results result;

    public String getName() {
        return name;
    }

    public Results getResult() {
        return result;
    }

    public DeleteUserResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String name, Results result) {
        super(Subjects.DeleteUserResponse, senderQueue, sender);

        this.name = name;
        this.result = result;
    }
}
