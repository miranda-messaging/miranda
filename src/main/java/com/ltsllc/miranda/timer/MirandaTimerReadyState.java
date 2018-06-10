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
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.timer.messages.CancelMessage;
import com.ltsllc.miranda.timer.messages.ScheduleOnceMessage;
import com.ltsllc.miranda.timer.messages.SchedulePeriodicMessage;
import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/12/2017.
 */
public class MirandaTimerReadyState extends State {

    private Logger logger = Logger.getLogger(MirandaTimerReadyState.class);

    public MirandaTimerReadyState(MirandaTimer timer) throws MirandaException {
        super(timer);
    }

    public MirandaTimer getTimer() {
        return (MirandaTimer) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case Cancel: {
                CancelMessage cancelMessage = (CancelMessage) message;
                nextState = processCancelMesssage(cancelMessage);
                break;
            }
            case ScheduleOnce: {
                ScheduleOnceMessage scheduleOnceMessage = (ScheduleOnceMessage) message;
                nextState = processScheduleOnceMessage(scheduleOnceMessage);
                break;
            }

            case SchedulePeriodic: {
                SchedulePeriodicMessage schedulePeriodicMessage = (SchedulePeriodicMessage) message;
                nextState = processSchedulePeriodicMessage(schedulePeriodicMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processScheduleOnceMessage(ScheduleOnceMessage scheduleOnceMessage) {
        getTimer().scheduleOnce(scheduleOnceMessage.getDelay(), scheduleOnceMessage.getReceiver(), scheduleOnceMessage.getMessage());

        return this;
    }


    private State processSchedulePeriodicMessage(SchedulePeriodicMessage message) {
        getTimer().schedulePeriodic(message.getDelay(), message.getPeriod(), message.getReceiver(), message.getMessage());

        return this;
    }

    public State processCancelMesssage(CancelMessage cancelMessage) {
        getTimer().cancel (cancelMessage.getReceiver());

        return this;
    }

}
