package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Subscription;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class SubscriptionsFile extends SingleFile<Subscription> {
    public SubscriptionsFile (BlockingQueue<Message> queue, String filename) {
        super(filename, queue);
    }
}
