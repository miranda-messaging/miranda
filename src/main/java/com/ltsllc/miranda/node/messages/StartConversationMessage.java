package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/11/2017.
 */
public class StartConversationMessage extends Message {
    private BlockingQueue<Message> respondTo;
    private String key;

    public StartConversationMessage(BlockingQueue<Message> senderQueue, Object sender, String key,
                                    BlockingQueue<Message> respondTo) {
        super(Subjects.StartConversation, senderQueue, sender);

        this.key = key;
        this.respondTo = respondTo;
    }

    public String getKey() {
        return key;
    }

    public BlockingQueue<Message> getRespondTo() {

        return respondTo;
    }
}
