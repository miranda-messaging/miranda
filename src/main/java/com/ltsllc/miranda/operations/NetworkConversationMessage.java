package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;

import java.util.concurrent.BlockingQueue;

/**
 * A {@link com.ltsllc.miranda.node.networkMessages.NetworkMessage} that is part of a conversation.
 */
public class NetworkConversationMessage extends NetworkMessage {
    private String conversation;
    private Node node;

    public String getConversation() {
        return conversation;
    }

    public Node getNode() {
        return node;
    }

    public NetworkConversationMessage(Subjects subject, BlockingQueue<Message> senderQueue, WireResponse response,
                                      String conversation, Node node) {

        super(subject, senderQueue, response);

        this.conversation = conversation;
        this.node = node;
    }
}
