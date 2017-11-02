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

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.messages.StopMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class ConnectionListenerReadyState extends State {
    public ConnectionListenerReadyState(ConnectionListener networkListener) {
        super(networkListener);
    }

    public ConnectionListener getNetworkListener () {
        return (ConnectionListener) getContainer();
    }

    public State start () {
        // getNetworkListener().getConnections();

        return getNetworkListener().getCurrentState();
    }

    public State processStopMessage (StopMessage stopMessage) {
        getNetworkListener().stopListening();
        return getNetworkListener().getCurrentState();
    }
}
