/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.timer;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/12/2017.
 */
public class MirandaTimerReadyState extends State {
    private static class LocalTimerTask extends TimerTask {
        private  static Logger logger = Logger.getLogger(LocalTimerTask.class);

        private Message message;
        private BlockingQueue<Message> queue;
        private BlockingQueue<Message> timer;

        public Message getMessage() {
            return message;
        }

        public LocalTimerTask (BlockingQueue<Message> queue, BlockingQueue<Message> timer, Message message) {
            this.queue = queue;
            this.timer = timer;
            this.message = message;
        }

        public void run () {
            try {
                queue.put(getMessage());
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
        LocalTimerTask localTimerTask = new LocalTimerTask(scheduleOnceMessage.getReceiver(), getTimer().getQueue(),
                scheduleOnceMessage.getMessage());
        timer.schedule(localTimerTask, scheduleOnceMessage.getDelay());

        return this;
    }


    private State processSchedulePeriodicMessage (SchedulePeriodicMessage message) {
        Timer timer = Miranda.timer.getTimer();
        LocalTimerTask localTimerTask = new LocalTimerTask(message.getReceiver(), getTimer().getQueue(), message.getMessage());
        timer.scheduleAtFixedRate(localTimerTask, message.getPeriod(), message.getPeriod());

        return this;
    }

}
