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

package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/24/2017.
 */
public class ClusterStartState extends State {
    @Override
    public State start() {
        getCluster().getClusterFile().sendLoad(getCluster().getQueue(), this);
        return this;
    }

    public Cluster getCluster() {
        return (Cluster) getContainer();
    }

    public ClusterStartState(Cluster cluster) throws MirandaException {
        super(cluster);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getCluster().getCurrentState();

        switch (message.getSubject()) {
            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage(fileLoadedMessage);
                break;
            }

            case FileDoesNotExist: {
                FileDoesNotExistMessage fileDoesNotExistMessage = (FileDoesNotExistMessage) message;
                nextState = processFileDoesNotExistMessage (fileDoesNotExistMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processFileLoadedMessage(FileLoadedMessage fileLoadedMessage) throws MirandaException {
        List<NodeElement> nodeElements = (List<NodeElement>) fileLoadedMessage.getData();
        List<Node> nodes = new ArrayList<Node>();

        for (NodeElement nodeElement : nodeElements) {
            Node node = new Node(nodeElement, getCluster().getNetwork(), getCluster());
            node.start();
            node.connect();
            nodes.add(node);
        }

        getCluster().setData(nodes);

        ClusterReadyState clusterReadyState = new ClusterReadyState(getCluster());
        return clusterReadyState;
    }

    public State processFileDoesNotExistMessage (FileDoesNotExistMessage fileDoesNotExistMessage) {
        getCluster().getClusterFile().sendCreateMessage(getCluster().getQueue(), this);
        return this;
    }
}
