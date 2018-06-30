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

package com.ltsllc.miranda.network.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.network.ConnectionListener;
import com.ltsllc.miranda.shutdown.ShutdownMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class ConnectionListenerReadyState extends State {
    public ConnectionListenerReadyState(ConnectionListener networkListener) throws MirandaException {
        super(networkListener);
    }

    public ConnectionListener getNetworkListener() {
        return (ConnectionListener) getContainer();
    }

    public State processMessage (Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default: {
                try {
                    nextState = super.processMessage(message);
                } catch (MirandaException e) {
                    Panic panic = new Panic(e, Panic.Reasons.ExceptionInProcessMessage);
                    Miranda.panicMiranda(panic);
                }
            }
        }

        return nextState;
    }


    public State start() {
        // getNetworkListener().getConnections();

        return getNetworkListener().getCurrentState();
    }

    public State processStopMessage(StopMessage stopMessage) {
        getNetworkListener().stopListening();
        return getNetworkListener().getCurrentState();
    }

    public State processShutdownMessage (ShutdownMessage shutdownMessage) {
        getNetworkListener().stopListening();
        return super.processShutdownMessage(shutdownMessage);
    }
}
