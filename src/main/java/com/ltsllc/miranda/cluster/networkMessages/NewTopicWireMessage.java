package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.topics.Topic;

/**
 * Created by Clark on 4/30/2017.
 */
public class NewTopicWireMessage extends WireMessage {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public NewTopicWireMessage(Topic topic) {
        super(WireSubjects.NewTopic);

        this.topic = topic;
    }
}
