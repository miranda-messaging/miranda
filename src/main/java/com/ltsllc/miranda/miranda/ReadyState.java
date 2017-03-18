package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.RemoteVersionMessage;
import com.ltsllc.miranda.cluster.messages.VersionsMessage;
import com.ltsllc.miranda.servlet.GetStatusMessage;
import com.ltsllc.miranda.servlet.GetStatusResponseMessage;
import com.ltsllc.miranda.servlet.StatusObject;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 2/10/2017.
 */
public class ReadyState extends State {
    private static Logger logger = Logger.getLogger(ReadyState.class);

    private Miranda miranda;

    public ReadyState (Miranda miranda) {
        super(miranda);

        this.miranda = miranda;
    }

    public Miranda getMiranda() {
        return miranda;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NewConnection: {
                NewConnectionMessage newConnectionMessage = (NewConnectionMessage) message;
                nextState = processNewConnectionMessage(newConnectionMessage);
                break;
            }

            case GetVersions: {
                GetVersionsMessage getVersionsMessage = (GetVersionsMessage) message;
                nextState = processGetVersionsMessage(getVersionsMessage);
                break;
            }

            case Versions: {
                VersionsMessage versionsMessage = (VersionsMessage) message;
                nextState = processVersionsMessage(versionsMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) message;
                nextState = processVersionMessage (versionMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case GetStatus: {
                GetStatusMessage getStatusMessage = (GetStatusMessage) message;
                nextState = processGetStatusMessage(getStatusMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    private State processNewConnectionMessage (NewConnectionMessage newConnectionMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this, getMiranda().getQueue());
        send(newConnectionMessage.getNode().getQueue(), getVersionMessage);
        return this;
    }


    private State processVersionsMessage (VersionsMessage versionsMessage) {
        for (NameVersion nameVersion : versionsMessage.getVersions()) {
            RemoteVersionMessage remoteVersionMessage = new RemoteVersionMessage(getMiranda().getQueue(), this, versionsMessage.getSender(), nameVersion);

            if (nameVersion.getName().equalsIgnoreCase("cluster")) {
                send(ClusterFile.getInstance().getQueue(), remoteVersionMessage);
            } else if (nameVersion.getName().equalsIgnoreCase("users")) {
                send(UsersFile.getInstance().getQueue(), remoteVersionMessage);
            } else if (nameVersion.getName().equalsIgnoreCase("topics")) {
                send(TopicsFile.getInstance().getQueue(), remoteVersionMessage);
            } else if (nameVersion.getName().equalsIgnoreCase("subscriptions")) {
                send(SubscriptionsFile.getInstance().getQueue(), remoteVersionMessage);
            }
        }

        return this;
    }


    private State processGetVersionsMessage (GetVersionsMessage getVersionsMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getMiranda().getQueue(), this, getMiranda().getQueue());

        send(Cluster.getInstance().getQueue(), getVersionMessage);
        send(UsersFile.getInstance().getQueue(), getVersionMessage);
        send(TopicsFile.getInstance().getQueue(), getVersionMessage);
        send(SubscriptionsFile.getInstance().getQueue(), getVersionMessage);

        GettingVersionsState gettingVersionsState= new GettingVersionsState(getMiranda(), getVersionsMessage.getSender());
        return gettingVersionsState;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        logger.error("processVersionMessage called");
        return this;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        GarbageCollectionMessage garbageCollectionMessage2 = new GarbageCollectionMessage(getMiranda().getQueue(), this);

        send(getMiranda().getCluster().getQueue(), garbageCollectionMessage2);
        send(getMiranda().getSubscriptions().getQueue(), garbageCollectionMessage2);
        send(getMiranda().getTopics().getQueue(), garbageCollectionMessage2);
        send(getMiranda().getUsers().getQueue(), garbageCollectionMessage2);

        return this;
    }

    private State processGetStatusMessage (GetStatusMessage getStatusMessage) {
        StatusObject statusObject = getMiranda().getStatusImpl();

        GetStatusResponseMessage response = new GetStatusResponseMessage(getMiranda().getQueue(), this, statusObject);
        getStatusMessage.reply(response);

        return this;
    }
}
