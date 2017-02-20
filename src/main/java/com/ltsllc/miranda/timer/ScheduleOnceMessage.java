package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class ScheduleOnceMessage extends ScheduleMessage {
    private long delay;

    public ScheduleOnceMessage (long delay, BlockingQueue<Message> senderQueue, Object sender) {
        super(Subjects.ScheduleOnce, senderQueue, sender);

        this.delay = delay;
    }

    public long getDelay() {
        return delay;
    }
}
