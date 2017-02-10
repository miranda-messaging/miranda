package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NodeElement;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class ClusterHealthCheckMessage extends Message {
    public ClusterHealthCheckMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.ClusterHealthCheck, senderQueue, sender);
    }
}
