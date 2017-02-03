package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class ConnectMessage extends Message {
    public ConnectMessage (BlockingQueue<Message> sender) {
        super(Subjects.Connect, sender);
    }

    public String toString () {
        return "connect";
    }
}
