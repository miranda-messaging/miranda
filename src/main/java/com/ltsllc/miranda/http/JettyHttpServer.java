package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletMapping;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.http.HttpServlet;
import java.util.List;

/**
 * Created by Clark on 3/9/2017.
 */
public class JettyHttpServer extends HttpServer {
    private static HandlerCollection ourHandlerCollection;
    private static ServletHandler ourServletHandler;

    private Server jetty;

    public JettyHttpServer (Server jetty, HandlerCollection handlerCollection) {
        super();

        this.jetty = jetty;
        ourHandlerCollection = handlerCollection;
    }



    public static ServletHandler getServletHandler() {
        return ourServletHandler;
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
            servletHandler.addServletWithMapping(mapping.getServletClass(), mapping.getPath());
        }

        getHandlerCollection().addHandler(servletHandler);
    }

    @Override
    public void startServer() {
        try {
            getJetty().setHandler(getHandlerCollection());
            getJetty().start();
        } catch (Exception e) {
            Panic panic = new StartupPanic("Excepion trying to start HttpServer", e, StartupPanic.StartupReasons.ExceptionStartingHttpServer);
            Miranda.getInstance().panic(panic);
        }
    }
}
