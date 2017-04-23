package com.ltsllc.miranda.miranda.operations;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/16/2017.
 */
public class Operation extends Consumer {
    private BlockingQueue<Message> requester;

    public BlockingQueue<Message> getRequester() {
        return requester;
    }

    public Operation (String name, BlockingQueue<Message> requester) {
        super(name);

        this.requester = requester;
    }
}
