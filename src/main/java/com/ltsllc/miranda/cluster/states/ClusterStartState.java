package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/24/2017.
 */
public class ClusterStartState extends State {
    public Cluster getCluster() {
        return (Cluster) getContainer();
    }

    public ClusterStartState (Cluster cluster) {
        super(cluster);
    }

    public State processMessage (Message message) {
        State nextState = getCluster().getCurrentState();

        switch (message.getSubject()) {
            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage (fileLoadedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) {
        List<NodeElement> nodeElements = (List<NodeElement>) fileLoadedMessage.getData();
        List<Node> nodes = new ArrayList<Node>();

        for (NodeElement nodeElement : nodeElements) {
            Node node = new Node(nodeElement, getCluster().getNetwork(), getCluster());
            node.start();
            node.connect();
            nodes.add(node);
        }

        getCluster().setNodes(nodes);

        ClusterReadyState clusterReadyState = new ClusterReadyState(getCluster());
        return clusterReadyState;
    }
}
