package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/12/2017.
 */
public class GarbageCollectionMessage extends Message {
    public GarbageCollectionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.GarbageCollection, senderQueue, sender);
    }
}
