package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class ScheduleMessage extends Message {
    private long delay;

    public ScheduleMessage(BlockingQueue<Message> sender, long delay) {
        super(Subjects.Schedule, sender);
        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }
}
