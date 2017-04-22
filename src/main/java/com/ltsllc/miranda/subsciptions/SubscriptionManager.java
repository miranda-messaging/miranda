package com.ltsllc.miranda.subsciptions;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/12/2017.
 */
public class SubscriptionManager extends Consumer {
    public static final String NAME = "SubscriptionManager";

    private SubscriptionsFile subscriptionsFile;

    public SubscriptionManager(String filename) {
        super("subscription manager");

        Miranda miranda = Miranda.getInstance();
        subscriptionsFile = new SubscriptionsFile(miranda.getWriter(), filename);

        SubscriptionManagerReadyState readyState = new SubscriptionManagerReadyState(this);
        setCurrentState(readyState);
    }

    public void sendOwnerQueryMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        OwnerQueryMessage ownerQueryMessage = new OwnerQueryMessage(senderQueue, sender, name);
        sendToMe(ownerQueryMessage);
    }

    public void performGarbageCollection () {
    }
}
