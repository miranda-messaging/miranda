package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.node.Node;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/13/2017.
 */
public class Response extends Message {
    private Node node;
    private Results result;

    public Results getResult() {
        return result;
    }

    public Node getNode() {
        return node;
    }

    public Response(Subjects subject, BlockingQueue<Message> senderQueue, Object sender, Node node, Results result) {
        super(subject, senderQueue, sender);

        this.node = node;
        this.result = result;
    }
}
