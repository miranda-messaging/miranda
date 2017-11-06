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
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;

import java.util.Timer;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/22/2017.
 */
public class MirandaTimer extends Consumer {
    private Timer timer;

    public MirandaTimer () throws MirandaException {
        super("timer");
        timer = new Timer("timer", true);
        MirandaTimerReadyState mirandaTimerReadyState = new MirandaTimerReadyState(this);
        setCurrentState(mirandaTimerReadyState);
    }

    public Timer getTimer() {
        return timer;
    }

    public void sendScheduleOnce(long delay, BlockingQueue<Message> receiver, Message message) {
        ScheduleOnceMessage scheduleOnceMessage = new ScheduleOnceMessage(null, this, delay,
                message, receiver);

        sendToMe(scheduleOnceMessage);
    }

    public void sendSchedulePeriodic(long period, BlockingQueue<Message> receiver, Message message) {
        SchedulePeriodicMessage periodicMessage = new SchedulePeriodicMessage(null, this, period,
                message, receiver);

        sendToMe(periodicMessage);
    }
}
