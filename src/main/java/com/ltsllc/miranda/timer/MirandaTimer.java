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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.timer.messages.CancelMessage;
import com.ltsllc.miranda.timer.messages.ScheduleOnceMessage;
import com.ltsllc.miranda.timer.messages.SchedulePeriodicMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class MirandaTimer extends Consumer {

    private static class LocalTimerTask extends TimerTask {
        private static Logger logger = Logger.getLogger(LocalTimerTask.class);

        private Message message;
        private BlockingQueue<Message> queue;


        public Message getMessage() {
            return message;
        }

        public LocalTimerTask(BlockingQueue<Message> receiver, Message message) {
            this.queue = receiver;
            this.message = message;
        }

        public void run() {
            try {
                queue.put(getMessage());
            } catch (InterruptedException e) {
                logger.error("Interrupted while sending message", e);
            }
        }
    }

    private Map<BlockingQueue<Message>, Timer> timerMap = new HashMap<>();

    public Map<BlockingQueue<Message>, Timer> getTimerMap() {
        return timerMap;
    }

    public MirandaTimer() throws MirandaException {
        super("timer");
        MirandaTimerReadyState mirandaTimerReadyState = new MirandaTimerReadyState(this);
        setCurrentState(mirandaTimerReadyState);
    }


    public void sendScheduleOnce(long delay, BlockingQueue<Message> receiver, Message message) {
        ScheduleOnceMessage scheduleOnceMessage = new ScheduleOnceMessage(null, this, delay,
                message, receiver);

        sendToMe(scheduleOnceMessage);
    }

    public void sendCancel (BlockingQueue<Message> senderQueue, Object senderObject, BlockingQueue<Message> receiver) {
        CancelMessage cancelMessage = new CancelMessage(senderQueue, senderObject, receiver);
        sendToMe(cancelMessage);
    }

    public void sendSchedulePeriodic(long delay, long period, BlockingQueue<Message> receiver, Message message) {
        SchedulePeriodicMessage periodicMessage = new SchedulePeriodicMessage(null, this,
                delay, period, message, receiver);

        sendToMe(periodicMessage);
    }

    public void scheduleOnce (long delay, BlockingQueue<Message> receiver, Message message) {
        LocalTimerTask localTimerTask = new LocalTimerTask(receiver, message);

        Timer timer = getTimerMap().get(receiver);
        if (timer == null) {
            timer = new Timer("timer", true);
            getTimerMap().put(receiver, timer);
        }

        timer.schedule(localTimerTask, delay);
    }


    public void schedulePeriodic (long delay, long period, BlockingQueue<Message> receiver, Message message) {
        LocalTimerTask localTimerTask = new LocalTimerTask(receiver, message);

        Timer timer = getTimerMap().get(receiver);
        if (timer == null) {
            timer = new Timer("timer", true);
            getTimerMap().put(receiver, timer);
        }

        timer.scheduleAtFixedRate(localTimerTask, delay, period);
    }

    public void cancel (BlockingQueue<Message> receiver) {
        Timer timer = getTimerMap().get(receiver);
        if (timer != null) {
            timer.cancel();
            getTimerMap().put(receiver, null);
        }
    }
}
