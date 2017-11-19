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

package com.ltsllc.miranda.network;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 5/23/2017.
 */
public class ConnectionListenerHolderReadyState extends State {
    public ConnectionListenerHolder getNetworkListnerHolder() {
        return (ConnectionListenerHolder) getContainer();
    }

    public ConnectionListenerHolderReadyState(ConnectionListenerHolder networkListenerHolder) throws MirandaException {
        super(networkListenerHolder);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getNetworkListnerHolder().getCurrentState();

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

    public State processShutdownMessage(ShutdownMessage shutdownMessage) throws MirandaException {
        getNetworkListnerHolder().stop();

        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(getNetworkListnerHolder().getQueue(),
                this, ConnectionListenerHolder.NAME);

        shutdownMessage.reply(shutdownResponseMessage);

        return StopState.getInstance();
    }

}
