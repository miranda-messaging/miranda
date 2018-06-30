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

package com.ltsllc.miranda.misc;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.ShutdownPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 5/15/2017.
 */
public class ThreadHolderReadyState extends State {
    public ThreadHolder getThreadHolder () {
        return (ThreadHolder) getContainer();
    }

    public ThreadHolderReadyState (ThreadHolder threadHolder) throws MirandaException {
        super(threadHolder);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getThreadHolder().getCurrentState();

        switch (message.getSubject()) {
            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processShutdownMessage (ShutdownMessage shutdownMessage) {
        try {
            getThreadHolder().shutdown();

            return StopState.getInstance();
        } catch (MirandaException e) {
            ShutdownPanic shutdownPanic = new ShutdownPanic(ShutdownPanic.ShutdownReasons.Exception, e);
            Miranda.panicMiranda(shutdownPanic);
            return this;
        }
    }
}
