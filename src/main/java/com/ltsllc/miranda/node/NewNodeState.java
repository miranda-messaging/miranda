package com.ltsllc.miranda.node;

/**
 * Created by Clark on 2/7/2017.
 */

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.*;
import com.ltsllc.miranda.file.GetFileResponseMessage;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.file.SubscriptionsFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.ConnectToMessage;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.GetUsersFileMessage;
import com.ltsllc.miranda.user.UsersFile;

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

            case Versions: {
                VersionsWireMessage versionsWireMessage = (VersionsWireMessage) networkMessage.getWireMessage();
                nextState = processVersionsWireMessag (versionsWireMessage);
                break;
            }

            case GetFileResponse: {
                GetFileResponseWireMessage getFileResponseWireMessage = (GetFileResponseWireMessage) networkMessage.getWireMessage();
                nextState = processGetFileResponseWireMessage (getFileResponseWireMessage);
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

            case Connect: {
                ConnectMessage connectMessage = (ConnectMessage) message;
                nextState = processConnectMessage(connectMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage (getFileMessage);
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


    private State processGetSubscriptionsFileMessage (GetSubscriptionsFileMessage getSubscriptionsFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("subscriptions");
        sendOnWire(getFileWireMessage);

        return this;
    }

    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }

    private State processConnectMessage (ConnectMessage connectMessage) {
        ConnectToMessage connectToMessage = new ConnectToMessage(connectMessage.getHost(), connectMessage.getPort(), getNode().getQueue(), this);
        send(getNode().getNetwork(), connectToMessage);

        return this;
    }


    private State processVersionsWireMessag (VersionsWireMessage versionsWireMessage) {
        VersionsMessage versionsMessage = new VersionsMessage(getNode().getQueue(), this, versionsWireMessage.getVersions());
        send(Miranda.getInstance().getQueue(), versionsMessage);

        return this;
    }


    private State processGetFileResponseWireMessage (GetFileResponseWireMessage getFileResponseWireMessage) {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getNode().getQueue(), this,
                getFileResponseWireMessage.getRequester(), getFileResponseWireMessage.getContents());

        if (getFileResponseWireMessage.getRequester().equalsIgnoreCase("cluster")) {
            send(ClusterFile.getInstance().getQueue(), getFileResponseMessage);
        } else if (getFileResponseWireMessage.getRequester().equalsIgnoreCase("users")) {
            send(UsersFile.getInstance().getQueue(), getFileResponseMessage);
        } else if (getFileResponseWireMessage.getRequester().equalsIgnoreCase("topics")) {
            send(TopicsFile.getInstance().getQueue(), getFileResponseMessage);
        } else if (getFileResponseWireMessage.getRequester().equalsIgnoreCase("subscriptions")) {
            send(SubscriptionsFile.getInstance().getQueue(), getFileResponseMessage);
        }

        return this;
    }

    private State processGetUsersFileMessage (GetUsersFileMessage getUsersFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("users");
        sendOnWire(getFileWireMessage);

        return this;
    }

    private State processGetTopicsFileMessage (GetTopicsFileMessage getTopicsFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("topics");
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetFileMessage (GetFileMessage getFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage(getFileMessage.getFile());
        sendOnWire(getFileWireMessage);

        return this;
    }
}