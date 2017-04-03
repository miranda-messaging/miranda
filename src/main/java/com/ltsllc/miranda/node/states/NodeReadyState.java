package com.ltsllc.miranda.node.states;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.file.GetFileResponseWireMessage;
import com.ltsllc.miranda.miranda.StopMessage;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.node.networkMessages.*;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.miranda.GetVersionsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.messages.GetUsersFileMessage;
import com.ltsllc.miranda.user.UsersFile;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeReadyState extends NodeState {
    private static Logger logger = Logger.getLogger(NodeReadyState.class);

    private Map<String, Version> versions = new HashMap<String, Version>();

    public NodeReadyState(Node node, Network network) {
        super(node, network);
    }

    public Map<String, Version> getVersions() {
        return versions;
    }


    public State processNetworkMessage(NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case GetVersions: {
                GetVersionsWireMessage getVersionsWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                nextState = processGetVersionsWireMessage(getVersionsWireMessage);
                break;
            }

            case Versions: {
                VersionsWireMessage versionsWireMessage = (VersionsWireMessage) networkMessage.getWireMessage();
                nextState = processVersionsWireMessage(versionsWireMessage);
                break;
            }

            case GetFile: {
                GetFileWireMessage getFileWireMessage = (GetFileWireMessage) networkMessage.getWireMessage();
                nextState = processGetFileWireMessage(getFileWireMessage);
                break;
            }

            case GetMessages: {
                GetMessagesWireMessage getMessagesWireMessage = (GetMessagesWireMessage) networkMessage.getWireMessage();
                nextState = processGetMessageWireMessage(getMessagesWireMessage);
                break;
            }

            case GetDeliveries: {
                GetDeliveriesWireMessage getDeliveriesWireMessage = (GetDeliveriesWireMessage) networkMessage.getWireMessage();
                nextState = processGetDeliveriesWireMessage(getDeliveriesWireMessage);
                break;
            }

            case NewSession: {
                NewSessionWireMessage newSessionWireMessage = (NewSessionWireMessage) networkMessage.getWireMessage();
                nextState = processNewSessionWireMessage (newSessionWireMessage);
                break;
            }

            case ExpiredSessions: {
                SessionsExpiredWireMessage sessionsExpiredWireMessage = (SessionsExpiredWireMessage) networkMessage.getWireMessage();
                nextState = processSessionsExpiredWireMessage(sessionsExpiredWireMessage);
                break;
            }

            default: {
                nextState = super.processNetworkMessage(networkMessage);
                break;
            }
        }

        return nextState;
    }


    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) message;
                nextState = processNetworkMessage (networkMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage (versionMessage);
                break;
            }

            case ConnectionClosed: {
                nextState = StopState.getInstance();
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) message;
                nextState = processGetClusterFileMessage(getClusterFileMessage);
                break;
            }

            case GetUsersFile: {
                GetUsersFileMessage getUsersFileMessage = (GetUsersFileMessage) message;
                nextState = processGetUsersFileMessage(getUsersFileMessage);
                break;
            }

            case Versions: {
                VersionsMessage versionsMessage = (VersionsMessage) message;
                nextState = processVersionsMessage (versionsMessage);
                break;
            }

            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponseMessage (getFileResponseMessage);
                break;
            }

            case SendNetworkMessage: {
                SendNetworkMessage sendNetworkMessage = (SendNetworkMessage) message;
                nextState = processSendNetworkMessage(sendNetworkMessage);
                break;
            }

            default :
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        getVersions().put(versionMessage.getNameVersion().getName(), versionMessage.getNameVersion().getVersion());

        if (versions.size() >= 1) {
            VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versionsToList(versionMessage.getNameVersion()));
            sendOnWire(versionsWireMessage);
        }

        return this;
    }


    private State processGetVersionsWireMessage (GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionsMessage getVersionsMessage = new GetVersionsMessage(getNode().getQueue(), this);
        send(Miranda.getInstance().getQueue(), getVersionsMessage);

        return this;
    }


    private List<NameVersion> versionsToList (NameVersion nameVersion) {
        List<NameVersion> list = new ArrayList<NameVersion>();

        list.add(nameVersion);

        return list;
    }


    public State processVersionsWireMessage (VersionsWireMessage versionsWireMessage) {
        VersionsMessage versionsMessage = new VersionsMessage(getNode().getQueue(), this, versionsWireMessage.getVersions());

        Consumer.staticSend (versionsMessage, Cluster.getInstance().getQueue());

        return this;
    }

    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("cluster");
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetFileWireMessage (GetFileWireMessage getFileWireMessage) {
        GetFileMessage getFileMessage = new GetFileMessage(getNode().getQueue(), this, getFileWireMessage.getFile());

        if (getFileWireMessage.getFile().equalsIgnoreCase(Cluster.FILE_NAME)) {
            send(Cluster.getInstance().getQueue(), getFileMessage);
        } else if (getFileWireMessage.getFile().equalsIgnoreCase(UsersFile.FILE_NAME)) {
            send(UsersFile.getInstance().getQueue(), getFileMessage);
        } else if (getFileWireMessage.getFile().equalsIgnoreCase(TopicsFile.FILE_NAME)) {
            send(TopicsFile.getInstance().getQueue(), getFileMessage);
        } else if (getFileWireMessage.getFile().equalsIgnoreCase(SubscriptionsFile.FILE_NAME)) {
            send(SubscriptionsFile.getInstance().getQueue(), getFileMessage);
        } else {
            logger.error ("Unknown file " + getFileWireMessage.getFile());
        }

        return this;
    }


    private State processVersionsMessage (VersionsMessage versionsMessage) {
        State nextState = this;

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versionsMessage.getVersions());
        sendOnWire(versionsWireMessage);

        return nextState;
    }


    private State processGetUsersFileMessage (GetUsersFileMessage getUsersFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("users");
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetFileResponseMessage (GetFileResponseMessage getFileResponseMessage) {
        GetFileResponseWireMessage getFileResponseWireMessage = new GetFileResponseWireMessage(getFileResponseMessage.getRequester(), getFileResponseMessage.getContents());
        sendOnWire(getFileResponseWireMessage);

        return this;
    }

    public State processStopMessage (StopMessage stopMessage) {
        StopWireMessage stopWireMessage = new StopWireMessage();
        getNetwork().sendNetworkMessage(getNode().getQueue(), this, getNode().getHandle(), stopWireMessage);

        return new NodeStoppingState(getNode());
    }

    public State processGetMessageWireMessage (GetMessagesWireMessage getMessagesWireMessage) {
        SystemMessages.getInstance().sendGetSystemMessages(getNode().getQueue(), this, getMessagesWireMessage.getFilename());

        return this;
    }

    public State processGetDeliveriesWireMessage (GetDeliveriesWireMessage getDeliveriesWireMessage) {
        SystemDeliveriesFile.getInstance().sendGetSystemDeliveries(getNode().getQueue(), this, getDeliveriesWireMessage.getFilename());

        return this;
    }

    public State processNewSessionWireMessage (NewSessionWireMessage newSessionWireMessage) {
        Miranda.getInstance().sendAddSessionMessage(getNode().getQueue(), this, newSessionWireMessage.getSession());

        return getNode().getCurrentState();
    }

    public State processSendNetworkMessage (SendNetworkMessage sendNetworkMessage) {
        getNode().sendOnWire(sendNetworkMessage.getWireMessage());

        return getNode().getCurrentState();
    }

    public State processSessionsExpiredWireMessage (SessionsExpiredWireMessage sessionsExpiredWireMessage) {
        Miranda.getInstance().sendSessionsExpiredMessage(getNode().getQueue(), this, sessionsExpiredWireMessage.getExpiredSessions());

        return getNode().getCurrentState();
    }
}
