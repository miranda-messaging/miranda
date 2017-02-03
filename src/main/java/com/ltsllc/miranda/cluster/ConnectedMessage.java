package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/2/2017.
 */
public class ConnectedMessage extends Message {
    public ConnectedMessage (BlockingQueue<Message> sender)
    {
        super(Subjects.Connected, sender);
    }
}
