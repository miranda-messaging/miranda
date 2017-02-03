package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectToMessage extends Message {
    private String host;
    private int port;

    public ConnectToMessage(String host, int port, BlockingQueue<Message> sender) {
        super(Subjects.ConnectTo, sender);
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

}
