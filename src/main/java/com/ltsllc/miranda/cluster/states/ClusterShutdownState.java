package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.NodeStoppedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import com.ltsllc.miranda.shutdown.ShutdownState;
import com.ltsllc.miranda.writer.WriteResponseMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class ClusterShutdownState extends ShutdownState {
    private Map<Node, Boolean> nodesShutDown;
    private boolean clusterFileWritten = false;

    public Cluster getCluster () {
        return (Cluster) getContainer();
    }

    public void nodeStopped (Node node) {
        nodesShutDown.put(node, true);
    }

    /**
     * Have all the nodes stopped?
     *
     * @return True if all the nodes are in the stopped state false otherwise.
     */
    public boolean allNodesStopped () {
        Set<Node> keySet = nodesShutDown.keySet();

        for (Node node : keySet) {
            if (null == nodesShutDown.get(node) || !nodesShutDown.get(node)) {
                return false;
            }
        }

        return true;
    }

    public ClusterShutdownState (BlockingQueue<Message> initiator)
    {
        super(initiator);

        nodesShutDown = new HashMap<>();
        clusterFileWritten = false;
    }

    public State start () {
        getCluster().shutdown();
        getCluster().getClusterFile().sendWriteMessage(getCluster().getQueue(), this);
        return this;
    }

    public State processMessage (Message message) {
        try {
            State nextState = null;

            switch (message.getSubject()) {
                case NodeStopped: {
                    NodeStoppedMessage nodeStoppedMessage = (NodeStoppedMessage) message;
                    nextState = processNodeStopped(nodeStoppedMessage);
                    break;
                }

                case WriteResponse: {
                    WriteResponseMessage writeResponseMessage = (WriteResponseMessage) message;
                    nextState = processFileWritten(writeResponseMessage);
                    break;
                }

                default: {
                    nextState = super.processMessage(message);
                    break;
                }
            }

            return nextState;
        } catch (MirandaException e) {
            Panic panic = new Panic(e, Panic.Reasons.ExceptionInProcessMessage);
            Miranda.panicMiranda(panic);
            return this;
        }
    }

    public State processFileWritten(WriteResponseMessage writeResponseMessage) {
        clusterFileWritten = true;

        if (allNodesStopped() && clusterFileWritten) {
            ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(getCluster().getQueue(),
                    this, getCluster().getName());
            send(getInitiator(), shutdownResponseMessage);
            return StopState.getInstance();
        }

        return this;
    }

    public State processNodeStopped(NodeStoppedMessage nodeStoppedMessage) {
        nodeStopped(nodeStoppedMessage.getNode());

        if (allNodesStopped() && clusterFileWritten) {
            ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(getCluster().getQueue(), this,
                    getCluster().getName());
            send (getInitiator(), shutdownResponseMessage);
            return StopState.getInstance();
        }

        return this;
    }
}
