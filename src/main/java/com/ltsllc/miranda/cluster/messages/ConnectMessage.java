package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */

/**
 * Connect to a host & port
 */
public class ConnectMessage extends Message {
    public ConnectMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.Connect, senderQueue, sender);
    }

    public String toString () {
        return "connect";
    }
}
