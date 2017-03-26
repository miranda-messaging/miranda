package com.ltsllc.miranda.cluster.states;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.SingleFileReadyState;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/6/2017.
 */
public class ClusterFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(ClusterFileReadyState.class);

    private ClusterFile clusterFile;

    public ClusterFileReadyState(ClusterFile clusterFile) {
        super(clusterFile);
        this.clusterFile = clusterFile;
    }

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    public static void setLogger(Logger logger) {
        ClusterFileReadyState.logger = logger;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case WriteSucceeded: {
                break;
            }

            case WriteFailed: {
                WriteFailedMessage writeFailedMessage = (WriteFailedMessage) message;
                nextState = processWriteFailedMessage(writeFailedMessage);
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) message;
                nextState = processGetClusterFileMessage(getClusterFileMessage);
                break;
            }

            case NodesUpdated: {
                NodesUpdatedMessage nodesUpdatedMessage = (NodesUpdatedMessage) message;
                nextState = processNodesUpdatedMessage(nodesUpdatedMessage);
                break;
            }

            case HealthCheckUpdate: {
                HealthCheckUpdateMessage healthCheckUpdateMessage = (HealthCheckUpdateMessage) message;
                nextState = processHealthCheckUpdateMessage(healthCheckUpdateMessage);
                break;
            }

            case Load: {
                LoadMessage loadMessage = (LoadMessage) message;
                nextState = processLoadMessage(loadMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        List<NodeElement> newList = new ArrayList<NodeElement>(getClusterFile().getData());

        ClusterFileMessage clusterFileMessage = new ClusterFileMessage (getClusterFile().getQueue(), this,
                newList, getClusterFile().getVersion());

        getClusterFileMessage.reply(clusterFileMessage);

        return this;
    }

    /**
     * This is called when there is a new version of the cluster file.  If
     * something changed, then we need to tell the cluster about it.
     *
     * @param newClusterFileMessage
     * @return
     */
    private State processNewClusterFileMessage (NewClusterFileMessage newClusterFileMessage) {
        if (!getClusterFile().getVersion().equals(newClusterFileMessage.getVersion())) {
            getClusterFile().merge(newClusterFileMessage.getFile());
        }

        return this;
    }

    /**
     * This message means that we should update all the matching nodes time
     * of last conection, and possibly drop the nodes that don't match.  A
     * node that has not connected in an amount of time (in milliseconds)
     * specifiede by {@link MirandaProperties#PROPERTY_CLUSTER_TIMEOUT}
     * should be dropped.
     *
     * @param healthCheckUpdateMessage
     * @return
     */
    private State processHealthCheckUpdateMessage (HealthCheckUpdateMessage healthCheckUpdateMessage) {
        //
        // update the time of last connect for nodes in the message
        //
        boolean nodesUpdated = false;
        for (NodeElement nodeElement : healthCheckUpdateMessage.getUpdates()) {
            NodeElement match = getClusterFile().matchingNode(nodeElement);
            if (null != match) {
                match.setLastConnected(System.currentTimeMillis());
                nodesUpdated = true;
            }
        }


        //
        // check to see if we should drop any nodes
        //
        long timeout = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_CLUSTER_TIMEOUT);
        long now = System.currentTimeMillis();
        List<NodeElement> drops = new ArrayList<NodeElement>();
        for (NodeElement nodeElement : getClusterFile().getData()) {
            long timeSinceLastConnect = now - nodeElement.getLastConnected();
            if (timeSinceLastConnect >= timeout)
                drops.add(nodeElement);
        }

        //
        // drop nodes
        //
        if (drops.size() > 0) {
            logger.info("dropping nodes that have timed out: " + drops);
            getClusterFile().getData().removeAll(drops);

            try {
                getClusterFile().updateVersion();
            } catch (NoSuchAlgorithmException e) {
                Panic panic = new Panic ("Exception trying to calculate version", e, Panic.Reasons.ExceptionTryingToCalculateVersion);
                Miranda.getInstance().panic(panic);
            }

            getClusterFile().write();

            for (NodeElement droppedNode : drops) {
                DropNodeMessage message = new DropNodeMessage(getClusterFile().getQueue(), this, droppedNode);
                send(getClusterFile().getCluster(), message);
            }
        }

        //
        // if we changed anything, update the version and write out the file
        //
        if (nodesUpdated || drops.size() > 0) {
            try {
                getClusterFile().updateVersion();
            } catch (NoSuchAlgorithmException e) {
                Panic panic = new Panic("Exception while caculating version", e, Panic.Reasons.ExceptionTryingToCalculateVersion);
                Miranda.getInstance().panic(panic);
            }

            getClusterFile().write();
        }

        return this;

    }

    /**
     * Send the version of the cluster file to the sender.
     *
     * @param getVersionMessage
     * @return
     */
    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("cluster", getClusterFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getClusterFile().getQueue(), this, nameVersion);
        getVersionMessage.reply(versionMessage);

        return this;
    }


    @Override
    public Type getListType() {
        return new TypeToken<List<NodeElement>>(){}.getType();
    }


    public void write () {
        WriteMessage writeMessage = new WriteMessage(getClusterFile().getFilename(), getClusterFile().getBytes(), getClusterFile().getQueue(), this);
        send (getClusterFile().getWriterQueue(), writeMessage);
    }


    @Override
    public boolean contains(Object o) {
        NodeElement nodeElement = (NodeElement) o;
        return getClusterFile().contains(nodeElement);
    }


    @Override
    public Version getVersion() {
        return getClusterFile().getVersion();
    }


    @Override
    public void add(Object o) {
        NodeElement nodeElement = (NodeElement) o;
        getClusterFile().getData().add(nodeElement);
    }


    @Override
    public SingleFile getFile() {
        return getClusterFile();
    }


    @Override
    public String getName() {
        return "clusters";
    }


    @Override
    public List<Perishable> getPerishables() {
        return new ArrayList<Perishable>(getClusterFile().getData());
    }


    @Override
    public String toString() {
        return "ReadyState";
    }

    private State processWriteFailedMessage (WriteFailedMessage message) {
        logger.error("Failed to write cluster file: " + message.getFilename(), message.getCause());

        return this;
    }

    public State start () {
        State nextState = super.start();

        MirandaProperties properties = Miranda.properties;

        long healthCheckPeriod = properties.getLongProperty(MirandaProperties.PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD);
        HealthCheckMessage healthCheckMessage = new HealthCheckMessage(getClusterFile().getCluster(), this);
        Miranda.timer.sendSchedulePeriodic(healthCheckPeriod, getClusterFile().getCluster(), healthCheckMessage);

        return nextState;
    }

    private State processLoadMessage (LoadMessage loadMessage) {
        getClusterFile().load();

        LoadResponseMessage loadResponseMessage = new LoadResponseMessage(getClusterFile().getCluster(), this, getClusterFile().getData());
        loadMessage.reply(loadResponseMessage);

        return this;
    }

    private State processNodesUpdatedMessage (NodesUpdatedMessage nodesUpdatedMessage) {
        List<NodeElement> copy = new ArrayList<NodeElement>(nodesUpdatedMessage.getNodeList());
        getClusterFile().setData(copy);
        getClusterFile().write();

        return this;
    }
}
