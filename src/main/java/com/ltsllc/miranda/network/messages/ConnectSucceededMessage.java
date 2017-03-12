package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/11/2017.
 */
public class ConnectSucceededMessage extends Message {
    private int handle;

    public int getHandle() {
        return handle;
    }

    public ConnectSucceededMessage (BlockingQueue<Message> senderQueue, Object sender, int handle) {
        super(Subjects.ConnectSucceeded, senderQueue, sender);

        this.handle = handle;
    }
}
