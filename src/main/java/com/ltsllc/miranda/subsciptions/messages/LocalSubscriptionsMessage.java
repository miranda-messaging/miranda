package com.ltsllc.miranda.subsciptions.messages;

import com.ltsllc.miranda.message.Message;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * A message that indicates that the local host must deliver messages for the indicated subscription
 */
public class LocalSubscriptionsMessage extends Message {
    private List<String> localSubscriptions;

    public List<String> getLocalSubscriptions() {
        return localSubscriptions;
    }

    public void setLocalSubscriptions(List<String> localSubscriptions) {
        this.localSubscriptions = localSubscriptions;
    }

    public LocalSubscriptionsMessage (BlockingQueue<Message> queue, Object senderObject, List<String> localSubscriptions) {
        super(Subjects.LocalSubscriptions, queue, senderObject);
        setLocalSubscriptions(localSubscriptions);
    }
}
