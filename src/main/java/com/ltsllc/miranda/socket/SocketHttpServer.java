package com.ltsllc.miranda.socket;

import com.ltsllc.miranda.server.HttpServer;
import org.eclipse.jetty.server.Server;

/**
 * Created by Clark on 3/4/2017.
 */
public class SocketHttpServer extends HttpServer {
    private Server jetty;

    public Server getJetty() {
        return jetty;
    }

    public SocketHttpServer (Server jetty) {
        this.jetty = jetty;
    }
}
