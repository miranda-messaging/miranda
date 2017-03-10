package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.servlet.ServletMapping;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/4/2017.
 */
abstract public class HttpServer extends Consumer {
    abstract public void addServlets (List<ServletMapping> servlets);
    abstract public void startServer ();

    public HttpServer () {
        super("http server");

        HttpReadyState httpReadyState = new HttpReadyState(this);
        setCurrentState(httpReadyState);
    }

    public void sendStart (BlockingQueue<Message> senderQueue) {
        StartHttpServerMessage startHttpServerMessage = new StartHttpServerMessage(senderQueue, this);
        send(startHttpServerMessage);
    }
}
