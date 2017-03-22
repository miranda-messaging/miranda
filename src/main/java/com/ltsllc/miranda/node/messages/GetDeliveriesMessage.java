package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/22/2017.
 */
public class GetDeliveriesMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public GetDeliveriesMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        super(Subjects.GetDeliveries, senderQueue, sender);

        this.filename = filename;
    }
}
