package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Consumer;

import java.util.TimerTask;

/**
 * Created by Clark on 2/10/2017.
 */
public class MirandaTimerTask extends TimerTask {
    private ScheduleMessage scheduleMessage;

    public ScheduleMessage getScheduleMessage() {
        return scheduleMessage;
    }

    public void run () {
        Consumer.staticSend(scheduleMessage.getMessage(), scheduleMessage.getSender());
    }
}
