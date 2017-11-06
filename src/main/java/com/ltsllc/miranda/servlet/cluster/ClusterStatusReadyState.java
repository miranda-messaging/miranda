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

package com.ltsllc.miranda.servlet.cluster;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.servlet.status.GetStatusResponseMessage;

/**
 * Created by Clark on 3/10/2017.
 */
public class ClusterStatusReadyState extends State {
    public ClusterStatusReadyState (ClusterStatus clusterStatus) throws MirandaException {
        super(clusterStatus);
    }

    public ClusterStatus getClusterStatus () {
        return (ClusterStatus) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case GetStatusResponse: {
                GetStatusResponseMessage getStatusResponseMessage = (GetStatusResponseMessage) message;
                nextState = processGetStatusResponseMessage (getStatusResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }
        return nextState;
    }

    private State processGetStatusResponseMessage (GetStatusResponseMessage getStatusResponseMessage) {
        getClusterStatus().receivedClusterStatus(getStatusResponseMessage);

        return this;
    }
}
