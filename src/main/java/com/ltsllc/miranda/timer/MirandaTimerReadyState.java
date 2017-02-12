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

    public MirandaTimerReadyState (Consumer consumer) {
        super(consumer);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Schedule: {
                ScheduleMessage scheduleMessage = (ScheduleMessage) message;
                nextState = processScheduleMessage (scheduleMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processScheduleMessage (ScheduleMessage scheduleMessage) {
        Timer timer = Miranda.timer.getTimer();
        LocalTimerTask localTimerTask = new LocalTimerTask(scheduleMessage.getSender(), Miranda.timer.getQueue());

        if (scheduleMessage.getType() == ScheduleMessage.ScheduleType.Once)
            timer.schedule(localTimerTask, scheduleMessage.getDelay());
        else if (scheduleMessage.getType() == ScheduleMessage.ScheduleType.Periodic)
            timer.scheduleAtFixedRate(localTimerTask, scheduleMessage.getDelay(), scheduleMessage.getDelay());
        else {
            logger.error ("Unknown schedule type " + scheduleMessage.getType());
        }

        return this;
    }

}
