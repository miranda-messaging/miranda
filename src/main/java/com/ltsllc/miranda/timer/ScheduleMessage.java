package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.timer.ScheduleMessage.ScheduleType.Once;

/**
 * Created by Clark on 1/22/2017.
 */
public class ScheduleMessage extends Message {
    public enum ScheduleType {
        Once,
        Periodic
    }

    private ScheduleType type;
    private long delay;
    private Message message;

    public ScheduleMessage(BlockingQueue<Message> sender, Object senderObject, long delay) {
        super(Subjects.Schedule, sender, senderObject);

        this.type = Once;
        this.delay = delay;
        this.message = new TimeoutMessage (getSender(), getSenderObject());
    }

    public ScheduleMessage (BlockingQueue<Message> senderQueue, Object sender, ScheduleType scheduleType, long delay, Message message) {
        super(Subjects.Schedule, senderQueue, sender);

        this.type = scheduleType;
        this.delay = delay;
        this.message = message;
    }

    public ScheduleType getType() {
        return type;
    }

    public Message getMessage() {
        return message;
    }

    public long getDelay() {
        return delay;
    }
}
