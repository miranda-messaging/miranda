package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.cluster.networkMessages.ConversationWireMessage;

import java.util.concurrent.BlockingQueue;

/**
 * Created by clarkhobbie on 6/12/17.
 */
public class BroadcastMessage extends Message {
    private String key;
    private ConversationWireMessage converasationWireMessage;

    public String getKey() {
        return key;
    }

    public ConversationWireMessage getWireMessage() {
        return converasationWireMessage;
    }

    public BroadcastMessage(BlockingQueue<Message> sender, Object senderObject, ConversationWireMessage wireMessage) {
        super(Subjects.Broadcast, sender, senderObject);
        this.converasationWireMessage = wireMessage;
    }
}
