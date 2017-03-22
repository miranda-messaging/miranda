package com.ltsllc.miranda.socket;

import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.servlet.objects.ServletMapping;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 * Created by Clark on 3/4/2017.
 */
public class SocketHttpServer extends HttpServer {
    @Override
    public void addServlets(List<ServletMapping> servlets) {
        throw new IllegalStateException("not implemented");
    }

    private Server jetty;

    public Server getJetty() {
        return jetty;
    }

    public SocketHttpServer (Server jetty) {
        this.jetty = jetty;
    }

    @Override
    public void startServer() {

    }
}
