package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.Perishable;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/12/2017.
 */
public class ExpiredMessage extends Message {
    private Collection<Perishable> expired;

    public Collection<Perishable> getExpired() {
        return expired;
    }

    public ExpiredMessage (BlockingQueue<Message> senderQueue, Object sender, Collection<Perishable> expired) {
        super(Subjects.Expired, senderQueue, sender);

        this.expired = expired;
    }
}
