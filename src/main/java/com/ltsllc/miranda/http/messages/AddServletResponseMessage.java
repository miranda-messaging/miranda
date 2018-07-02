package com.ltsllc.miranda.http.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

public class AddServletResponseMessage extends Message {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public AddServletResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String path) {
        super(Subjects.AddServletResponse, senderQueue, sender);
        setPath(path);
    }
}
