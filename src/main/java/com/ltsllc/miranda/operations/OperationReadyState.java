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

package com.ltsllc.miranda.operations;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 4/16/2017.
 */
public class OperationReadyState extends State {
    private static Logger logger = Logger.getLogger(OperationReadyState.class);

    public OperationReadyState(Consumer consumer) throws MirandaException {
        super(consumer);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = StopState.getInstance();

        switch (message.getSubject()) {
            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
            }
        }

        return nextState;
    }

    public State processShutdownMessage(ShutdownMessage shutdownMessage) {
        logger.warn(this + " is shutting down");
        return StopState.getInstance();
    }
}
