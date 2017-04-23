package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class OwnerQueryResponseMessage extends Message {
    private String owner;
    private List<String> property;
    private String sendingManager;

    public String getSendingManager() {
        return sendingManager;
    }

    public List<String> getProperty() {
        return property;
    }

    public String getOwner() {

        return owner;
    }

    public OwnerQueryResponseMessage (BlockingQueue<Message> senderQueue, Object sender, String owner,
                                      List<String> property, String sendingManager) {
        super(Subjects.OwnerQueryResponse, senderQueue, sender);

        this.owner = owner;
        this.property = property;
        this.sendingManager = sendingManager;
    }
}
