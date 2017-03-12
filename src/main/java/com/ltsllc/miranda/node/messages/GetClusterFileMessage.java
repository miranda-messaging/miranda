package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/8/2017.
 */
public class GetClusterFileMessage extends Message {
    public GetClusterFileMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetClusterFile, senderQueue, sender);
    }
}
