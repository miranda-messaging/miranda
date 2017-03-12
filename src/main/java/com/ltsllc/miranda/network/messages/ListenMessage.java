package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class ListenMessage extends Message {
    private int port;

    public ListenMessage (BlockingQueue<Message> queue, int port, Object senderObject) {
        super(Subjects.Listen, queue, senderObject);
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
