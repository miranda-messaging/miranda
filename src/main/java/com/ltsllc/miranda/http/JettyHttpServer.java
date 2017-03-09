package com.ltsllc.miranda.http;

import com.ltsllc.miranda.servlet.ServletMapping;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletHandler;

import java.util.List;

/**
 * Created by Clark on 3/9/2017.
 */
public class JettyHttpServer extends HttpServer {
    private static HandlerCollection ourHandlerCollection;

    private Server jetty;

    public JettyHttpServer (Server jetty, HandlerCollection handlerCollection) {
        super();

        this.jetty = jetty;
        ourHandlerCollection = handlerCollection;
    }

    public static HandlerCollection getHandlerCollection () {
        return ourHandlerCollection;
    }

    public Server getJetty () {
        return jetty;
    }

    @Override
    public void addServlets(List<ServletMapping> servlets) {
        ServletHandler servletHandler = new ServletHandler();

        for (ServletMapping mapping : servlets) {
            servletHandler.addServletWithMapping(mapping.getServlet(), mapping.getPath());
        }

        getHandlerCollection().addHandler(servletHandler);
    }
}
