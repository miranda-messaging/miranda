package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.GetFileMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/5/2017.
 */
public class GetStatusMessage extends Message {
    public GetStatusMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetStatus, senderQueue, sender);
    }
}
