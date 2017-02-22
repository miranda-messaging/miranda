package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */

/**
 * This means it's time to do a health check.
 */
public class HealthCheckMessage extends Message {
    public HealthCheckMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.HealthCheck, senderQueue, sender);
    }
}
