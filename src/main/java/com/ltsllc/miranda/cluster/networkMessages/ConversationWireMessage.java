package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * A {@link WireMessage} that is part of a conversation
 */
public class ConversationWireMessage extends WireMessage {
    private String key;

    public ConversationWireMessage (WireSubjects subject, String key) {
        super(subject);

        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
