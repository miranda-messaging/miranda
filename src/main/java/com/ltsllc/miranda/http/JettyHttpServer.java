/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.catchall.CatchallServlet;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletHandler;

import java.util.List;

/**
 * Created by Clark on 3/9/2017.
 */
public class JettyHttpServer extends HttpServer {
    private static HandlerCollection ourHandlerCollection;
    private static ServletHandler ourServletHandler;
    private static Logger logger = Logger.getLogger(JettyHttpServer.class);

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
            ServletHandler servletHandler = new ServletHandler();
            servletHandler.addServletWithMapping(CatchallServlet.class, "/");
            // getHandlerCollection().addHandler(servletHandler);
            getJetty().setHandler(getHandlerCollection());
            getJetty().start();
            logger.info("Jetty started");
        } catch (Exception e) {
            Panic panic = new StartupPanic("Excepion trying to start HttpServer", e, StartupPanic.StartupReasons.ExceptionStartingHttpServer);
            Miranda.getInstance().panic(panic);
        }
    }
}
