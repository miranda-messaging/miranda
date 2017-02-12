package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetVersionsMessage extends Message {
    private Node requester;

    public GetVersionsMessage (BlockingQueue<Message> senderQueue, Object sender, Node requester) {
        super(Subjects.GetVersions, senderQueue, sender);

        this.requester = requester;
    }

    public Node getRequester() {
        return requester;
    }
}