package com.ltsllc.miranda.node;

/**
 * Created by Clark on 2/7/2017.
 */

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.NewNodeMessage;

/**
 * When a node is added via a connect it enters this state, waiting for a join message.
 */
public class NewNodeState extends NodeState {
    public NewNodeState (Node node) {
        super(node);
    }




    @Override
    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        WireMessage wireMessage = networkMessage.getWireMessage();

        switch (wireMessage.getWireSubject()) {
            case Join: {
                JoinWireMessage joinWireMessage = (JoinWireMessage) networkMessage.getWireMessage();
                nextState = processJoinWireMessage (joinWireMessage);
                break;
            }

            default:
                nextState = super.processNetworkMessage(networkMessage);
                break;
        }

        return nextState;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processJoinWireMessage (JoinWireMessage joinWireMessage) {
        State nextState = this;

        getNode().setDns(joinWireMessage.getDns());
        getNode().setIp(joinWireMessage.getIp());
        getNode().setPort(joinWireMessage.getPort());
        getNode().setDescription(joinWireMessage.getDescription());

        Cluster.nodeAdded(getNode().getQueue(), this, joinWireMessage.getDns(), joinWireMessage.getIp(), joinWireMessage.getPort(), joinWireMessage.getDescription());

        JoinSuccessWireMessage joinSuccessWireMessage = new JoinSuccessWireMessage();
        sendOnWire(joinSuccessWireMessage);

        SyncingState syncingState = new SyncingState(getNode());

        return syncingState;
    }
}
