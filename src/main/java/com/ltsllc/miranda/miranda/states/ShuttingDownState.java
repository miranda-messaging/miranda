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

package com.ltsllc.miranda.miranda.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

/**
 * Miranda is shutting down.  When it enters this state, it is waiting for
 * a ready message from each of its subsystems.  When it gets those it terminates.
 */
public class ShuttingDownState extends State {
    private static Logger logger = Logger.getLogger(ShuttingDownState.class);

    public static void setLogger(Logger logger) {
        ShuttingDownState.logger = logger;
    }

    public Miranda getMiranda() {
        return (Miranda) getContainer();
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getMiranda().getCurrentState();

        switch (message.getSubject()) {
            case ShutdownResponse: {
                ShutdownResponseMessage shutdownResponseMessage = (ShutdownResponseMessage) message;
                nextState = processShutdownResponseMessage(shutdownResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }


    public ShuttingDownState(Miranda miranda) throws MirandaException {
        super(miranda);
    }

    public State processShutdownResponseMessage(ShutdownResponseMessage shutdownResponseMessage) {
        if (getMiranda().readyToShutDown()) {
            logger.info("System is shutting down");
            return StopState.getInstance();
        }

        return getMiranda().getCurrentState();
    }

}
