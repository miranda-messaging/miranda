package com.ltsllc.miranda.servlet.objects;

/**
 * Created by Clark on 3/4/2017.
 */
public class ConnectionObject {
    private String host;
    private int port;

    public int getPort() {
        return port;
    }

    public String getHost() {

        return host;
    }

    public ConnectionObject (String host, int port) {
        this.host = host;
        this.port = port;
    }
}
