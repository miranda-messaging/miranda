package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/10/2017.
 */
public class DoneSynchronizingMessage extends Message {
    public DoneSynchronizingMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.DoneSynchronizing, senderQueue, sender);
    }
}
