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

package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.Node;

/**
 * A new node enters this state when it is trying to connect to a remote
 * system.  The state assumes that the network has been asked to connect to
 * the remote host.
 */
public class NodeStartState extends NodeState {
    public NodeStartState (Node node, Network network) {
        super(node, network);
    }

    public State processMessage (Message m) {
        State nextState = null;

        switch (m.getSubject()) {
            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) m;
                nextState = processConnectMessage(connectMessage);
                break;
            }

            default :
                nextState = super.processMessage(m);
                break;
        }

        return nextState;
    }


    private State processConnectMessage (ConnectMessage connectMessage) {
        getNode().connect();

        ConnectingState connectingState = new ConnectingState(getNode(), getNetwork());
        return connectingState;
    }
}
