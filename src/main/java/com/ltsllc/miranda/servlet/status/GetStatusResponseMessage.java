package com.ltsllc.miranda.servlet.status;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/9/2017.
 */
public class GetStatusResponseMessage extends Message {
    private Object statusObject;

    public Object getStatusObject() {
        return statusObject;
    }

    public GetStatusResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Object statusObject) {
        super(Subjects.GetStatusResponse, senderQueue, sender);

        this.statusObject = statusObject;
    }
}
