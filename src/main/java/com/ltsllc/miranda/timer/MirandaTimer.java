package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StartState;
import com.ltsllc.miranda.State;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
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
}
