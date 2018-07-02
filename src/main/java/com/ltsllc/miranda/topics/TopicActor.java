package com.ltsllc.miranda.topics;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.basicclasses.Topic;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.http.ServletMapping;
import com.ltsllc.miranda.servlet.receivemessage.ReceiveMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;

public class TopicActor extends Consumer {
    private Topic topic;
    private SubscriptionManager subscriptionManager;
    private HttpServer httpServer;

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public SubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    public void setSubscriptionManager(SubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public TopicActor (Topic topic, SubscriptionManager subscriptionManager, HttpServer httpServer) {
        setHttpServer(httpServer);
        setSubscriptionManager(subscriptionManager);
        setTopic(topic);
        intialize();
    }

    public void intialize() {
        String path = "/publisher/" + getTopic().getName();
        ServletMapping servletMapping = new ServletMapping(path, ReceiveMessage.class);
        getHttpServer().sendAddServlet(getQueue(), this,servletMapping);
    }
}
