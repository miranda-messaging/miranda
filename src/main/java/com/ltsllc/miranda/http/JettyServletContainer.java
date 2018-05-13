package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

/**
 * A ServletContainer for JettySSL
 */
public class JettyServletContainer extends ServletContainer {
    private static Logger logger = Logger.getLogger(JettyServletContainer.class);

    private Server jetty;

    public JettyServletContainer(Server jetty) {
        this.jetty = jetty;
    }

    public Server getJetty() {
        return jetty;
    }

    /**
     * Start the container, registering all the servlets first.
     */
    public void startContainer() {
        try {
            ServletHandler servletHandler = new ServletHandler();

            for (ServletMapping servletMapping : getServlets()) {
                servletHandler.addServletWithMapping(servletMapping.getServletClass(), servletMapping.getPath());
            }

            getJetty().setHandler(servletHandler);
            getJetty().start();
            logger.info("JettySSL started");
        } catch (Exception e) {
            StartupPanic startupPanic = new StartupPanic("Exception starting servlet container", e,
                    StartupPanic.StartupReasons.ExceptionStartingServletContainer);

            Miranda.panicMiranda(startupPanic);
        }
    }

    /**
     * Stop the servlet container.
     */
    public void stopContainer() {
        try {
            getJetty().stop();
        } catch (Exception e) {
            Panic panic = new Panic("Exception shutting down servlet container", e,
                    Panic.Reasons.ExceptionShuttingDownServletContainer);
        }
    }
}
