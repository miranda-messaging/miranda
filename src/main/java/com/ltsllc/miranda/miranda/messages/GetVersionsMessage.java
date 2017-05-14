package com.ltsllc.miranda.miranda.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetVersionsMessage extends Message {
    public GetVersionsMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GetVersions, senderQueue, sender);
    }
}