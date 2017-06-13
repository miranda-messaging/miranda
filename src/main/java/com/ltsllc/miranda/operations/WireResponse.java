package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 6/13/2017.
 */
public class WireResponse extends WireMessage {
    private Results result;
    private String conversation;

    public Results getResult() {
        return result;
    }

    public String getConversation() {
        return conversation;
    }

    public WireResponse (WireSubjects subject, String conversation, Results result) {
        super(subject);

        this.result = result;
        this.conversation = conversation;
    }
}
