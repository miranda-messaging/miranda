package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.miranda.Miranda.timer;

/**
 * Created by Clark on 2/12/2017.
 */
public class MirandaTimerReadyState extends State {
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

    private Logger logger = Logger.getLogger(MirandaTimerReadyState.class);

    public MirandaTimerReadyState (MirandaTimer timer) {
        super(timer);
    }

    public MirandaTimer getTimer () {
        return (MirandaTimer) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case ScheduleOnce: {
                ScheduleOnceMessage scheduleOnceMessage = (ScheduleOnceMessage) message;
                nextState = processScheduleOnceMessage (scheduleOnceMessage);
                break;
            }

            case SchedulePeriodic: {
                SchedulePeriodicMessage schedulePeriodicMessage = (SchedulePeriodicMessage) message;
                nextState = processSchedulePeriodicMessage(schedulePeriodicMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processScheduleOnceMessage (ScheduleOnceMessage scheduleOnceMessage) {
        Timer timer = Miranda.timer.getTimer();
        LocalTimerTask localTimerTask = new LocalTimerTask(scheduleOnceMessage.getSender(), getTimer().getQueue());
        timer.schedule(localTimerTask, scheduleOnceMessage.getDelay());

        return this;
    }


    private State processSchedulePeriodicMessage (SchedulePeriodicMessage message) {
        Timer timer = Miranda.timer.getTimer();
        LocalTimerTask localTimerTask = new LocalTimerTask(message.getReceiver(), getTimer().getQueue());
        timer.scheduleAtFixedRate(localTimerTask, message.getPeriod(), message.getPeriod());

        return this;
    }

}
