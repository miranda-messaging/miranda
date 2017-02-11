package com.ltsllc.miranda.node;

/**
 * Created by Clark on 2/7/2017.
 */

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.NewNodeMessage;
import com.ltsllc.miranda.user.GetUsersFileMessage;

import java.util.Stack;

/**
 * When a node is added via a connect it enters this state, waiting for a join message.
 */
public class NewNodeState extends NodeState {
    private Stack<Message> stack = new Stack<Message>();

    public NewNodeState (Node node) {
        super(node);
    }


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

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) message;
                nextState = processGetClusterFileMessage(getClusterFileMessage);
                break;
            }


            case GetUsersFile: {
                GetUsersFileMessage getUsersFileMessage = (GetUsersFileMessage) message;
                nextState = processGetUsersFileMessage (getUsersFileMessage);
                break;
            }


            case GetTopicsFile: {
                GetTopicsFileMessage getTopicsFileMessage = (GetTopicsFileMessage) message;
                nextState = processGetTopicsFileMessage (getTopicsFileMessage);
                break;
            }

            case GetSubscriptionsFile: {
                GetSubscriptionsFileMessage getSubscriptionsFileMessage = (GetSubscriptionsFileMessage) message;
                nextState = processGetSubscriptionsFileMessage(getSubscriptionsFileMessage);
                break;
            }

            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage (getVersionMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        State nextState = this;

        stack.push(getClusterFileMessage);

        return nextState;
    }

    private State processJoinWireMessage (JoinWireMessage joinWireMessage) {
        getNode().setDns(joinWireMessage.getDns());
        getNode().setIp(joinWireMessage.getIp());
        getNode().setPort(joinWireMessage.getPort());
        getNode().setDescription(joinWireMessage.getDescription());

        NodeUpdatedMessage nodeUpdatedMessage = new NodeUpdatedMessage(getNode().getQueue(), this, getNode());
        send(Cluster.getInstance().getQueue(), nodeUpdatedMessage);

        JoinSuccessWireMessage joinSuccessWireMessage = new JoinSuccessWireMessage();
        sendOnWire(joinSuccessWireMessage);

        SyncingState syncingState = new SyncingState(getNode());

        return syncingState;
    }


    private State processGetUsersFileMessage (GetUsersFileMessage getUsersFileMessage) {
        GetUsersFileWireMessage getUsersFileWireMessage = new GetUsersFileWireMessage();
        sendOnWire(getUsersFileWireMessage);
        return this;
    }


    private State processGetTopicsFileMessage (GetTopicsFileMessage getTopicsFileMessage) {
        GetTopicsFileWireMessage getTopicsFileWireMessage = new GetTopicsFileWireMessage();
        sendOnWire(getTopicsFileWireMessage);
        return this;
    }


    private State processGetSubscriptionsFileMessage (GetSubscriptionsFileMessage getSubscriptionsFileMessage) {
        GetSubscriptionsFileWireMessage getSubscriptionsFileWireMessage = new GetSubscriptionsFileWireMessage();
        sendOnWire(getSubscriptionsFileWireMessage);

        return this;
    }

    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }
}
