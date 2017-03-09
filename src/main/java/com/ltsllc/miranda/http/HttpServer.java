package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.servlet.ServletMapping;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerCollection;

import java.util.List;

/**
 * Created by Clark on 3/4/2017.
 */
abstract public class HttpServer extends Consumer {
    abstract public void addServlets (List<ServletMapping> servlets);

    public HttpServer () {
        super("http server");

        HttpReadyState httpReadyState = new HttpReadyState(this);
        setCurrentState(httpReadyState);
    }
}
