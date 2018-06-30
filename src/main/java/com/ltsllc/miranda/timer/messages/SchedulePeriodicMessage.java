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

package com.ltsllc.miranda.timer.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class SchedulePeriodicMessage extends ScheduleMessage {
    private long period;
    private long delay;

    public void setPeriod(long period) {
        this.period = period;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public SchedulePeriodicMessage(BlockingQueue<Message> senderQueue, Object sender, long delay,
                                   long period, Message message, BlockingQueue<Message> receiver) {
        super(Subjects.SchedulePeriodic, senderQueue, sender, receiver, message);

        setPeriod(period);
        setDelay(delay);
    }

    public long getPeriod() {
        return period;
    }
}
