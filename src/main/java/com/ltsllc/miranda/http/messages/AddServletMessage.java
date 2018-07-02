package com.ltsllc.miranda.http.messages;

import com.ltsllc.miranda.http.ServletMapping;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Add another servlet to the HttpServer
 */
public class AddServletMessage extends Message {
    private ServletMapping servletMapping;

    public ServletMapping getServletMapping() {
        return servletMapping;
    }

    public void setServletMapping(ServletMapping servletMapping) {
        this.servletMapping = servletMapping;
    }

    public AddServletMessage (BlockingQueue<Message> senderQueue, Object sender, ServletMapping servletMapping) {
        super(Subjects.AddServlet, senderQueue, sender);
        setServletMapping(servletMapping);
    }
}
