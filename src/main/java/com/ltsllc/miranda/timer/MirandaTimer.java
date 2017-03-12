package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class MirandaTimer extends Consumer {
    private Timer timer;

    public MirandaTimer () {
        super("timer");
        timer = new Timer("timer", true);
        MirandaTimerReadyState mirandaTimerReadyState = new MirandaTimerReadyState(this);
        setCurrentState(mirandaTimerReadyState);
    }

    public Timer getTimer() {
        return timer;
    }

    public void scheduleOnce (long delay, BlockingQueue<Message> receiver, Message message) {
        ScheduleOnceMessage scheduleOnceMessage = new ScheduleOnceMessage(null, this, delay,
                message, receiver);

        sendToMe(scheduleOnceMessage);
    }

    public void schedulePeriodic (long period, BlockingQueue<Message> receiver, Message message) {
        SchedulePeriodicMessage periodicMessage = new SchedulePeriodicMessage(null, this, period,
                message, receiver);

        sendToMe(periodicMessage);
    }
}
