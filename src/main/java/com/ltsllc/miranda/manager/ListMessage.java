package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 6/8/2017.
 */
public class ListMessage extends Message {
    public ListMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.List, senderQueue, sender);
    }
}
