package com.ltsllc.miranda.http;

import com.ltsllc.miranda.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by miranda on 7/21/2017.
 */
public class AddServletsMessage extends Message {
    private List<ServletMapping> servlets;

    public AddServletsMessage(BlockingQueue<Message> sender, Object senderObject, List<ServletMapping> servlets) {
        super(Subjects.AddServlets, sender, senderObject);
        this.servlets = servlets;
    }

    public List<ServletMapping> getServlets() {
        return servlets;
    }
}
