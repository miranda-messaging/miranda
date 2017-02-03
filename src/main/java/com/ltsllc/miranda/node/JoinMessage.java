package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class JoinMessage extends WireMessage {
    public JoinMessage (BlockingQueue<Message> queue)
    {
        super (WireSubjects.Join);
    }
}
