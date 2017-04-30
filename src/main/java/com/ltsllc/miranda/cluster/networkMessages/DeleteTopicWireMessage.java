package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.topics.Topic;

/**
 * Created by Clark on 4/30/2017.
 */
public class DeleteTopicWireMessage extends WireMessage {
    private String name;

    public String getName() {
        return name;
    }

    public DeleteTopicWireMessage(String name) {
        super(WireSubjects.DeleteTopic);

        this.name = name;
    }
}
