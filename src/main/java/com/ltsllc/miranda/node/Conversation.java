package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/12/2017.
 */
public class Conversation {
    private String key;
    private BlockingQueue<Message> receiver;

    public Conversation(String key, BlockingQueue<Message> receiver) {
        this.key = key;
        this.receiver = receiver;
    }

    public BlockingQueue<Message> getReceiver() {
        return receiver;
    }

    public String getKey() {
        return key;
    }

    public void forwardMessage(ConversationMessage message) {
        Consumer.staticSend(message.getMessage(), getReceiver());
    }
}
