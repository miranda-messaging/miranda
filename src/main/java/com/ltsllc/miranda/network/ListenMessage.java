package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class ListenMessage extends Message {
    private int port;

    public ListenMessage (BlockingQueue<Message> queue, int port) {
        super(Subjects.Listen, queue);
        this.port = port;
    }

    public int getPort() {
        return port;
    }
}
