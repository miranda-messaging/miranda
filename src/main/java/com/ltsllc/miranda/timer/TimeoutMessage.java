package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class TimeoutMessage extends Message {
    public TimeoutMessage(BlockingQueue<Message> sender) {
        super(Subjects.Timeout, sender);
    }
}
