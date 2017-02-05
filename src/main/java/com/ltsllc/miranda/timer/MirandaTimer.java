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
    private static class LocalTimerTask extends TimerTask {
        private  static Logger logger = Logger.getLogger(LocalTimerTask.class);

        private BlockingQueue<Message> queue;
        private BlockingQueue<Message> timer;

        public LocalTimerTask (BlockingQueue<Message> queue, BlockingQueue<Message> timer) {
            this.queue = queue;
            this.timer = timer;
        }

        public void run () {
            try {
                TimeoutMessage m = new TimeoutMessage(timer, this);
                queue.put(m);
            } catch (InterruptedException e) {
                logger.error ("Interrupted while sending message", e);
            }
        }
    }

    private Timer timer;

    public MirandaTimer () {
        super("timer");
        timer = new Timer("timer", true);
        setCurrentState(StartState.getInstance());
    }

    public State processMessage (Message m) {
        switch (m.getSubject()) {
            case Schedule: {
                ScheduleMessage schedule = (ScheduleMessage) m;
                processSchedule(schedule);
                break;
            }

            default:
                super.processMessage(m);
                break;
        }

        return StartState.getInstance();
    }


    private void processSchedule (ScheduleMessage schedule) {
        LocalTimerTask localTimerTask = new LocalTimerTask(schedule.getSender(), getQueue());
        timer.schedule(localTimerTask, schedule.getDelay());
    }
}
