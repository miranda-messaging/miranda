package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/11/2017.
 */
public class EndConversationMessage extends Message {
    private String key;

    public EndConversationMessage(BlockingQueue<Message> senderQueue, Object sender, String key) {
        super(Subjects.EndConversation, senderQueue, sender);

        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
