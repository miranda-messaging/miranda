package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/23/2017.
 */
public class GetSystemDeliveriesMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public GetSystemDeliveriesMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        super(Subjects.GetDeliveries, senderQueue, sender);

        this.filename = filename;
    }
}
