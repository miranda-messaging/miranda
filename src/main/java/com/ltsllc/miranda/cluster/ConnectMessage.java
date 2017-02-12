package com.ltsllc.miranda.cluster;

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
    private String host;
    private int port;

    public ConnectMessage (BlockingQueue<Message> senderQueue, Object sender, String host, int port) {
        super(Subjects.Connect, senderQueue, sender);

        this.host = host;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {

        return host;
    }

    public String toString () {
        return "connect";
    }
}
