package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class GetTopicsFileMessage extends Message {
    public GetTopicsFileMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetTopicsFile, senderQueue, sender);
    }
}
