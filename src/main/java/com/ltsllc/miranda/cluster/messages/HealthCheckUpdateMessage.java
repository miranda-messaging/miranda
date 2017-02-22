package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NodeElement;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class HealthCheckUpdateMessage extends Message {
    private List<NodeElement> updates;

    public HealthCheckUpdateMessage (BlockingQueue<Message> senderQueue, Object sender, List<NodeElement> updates) {
        super(Subjects.HealthCheckUpdate, senderQueue, sender);

        this.updates = updates;
    }

    public List<NodeElement> getUpdates() {
        return updates;
    }
}
