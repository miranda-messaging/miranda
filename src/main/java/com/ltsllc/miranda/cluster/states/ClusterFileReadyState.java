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

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.LoadResponseMessage;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.*;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.states.SingleFileReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.messages.GetClusterFileMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/6/2017.
 */
public class ClusterFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(ClusterFileReadyState.class);

    public ClusterFileReadyState(ClusterFile clusterFile) throws MirandaException {
        super(clusterFile);
    }

    public ClusterFile getClusterFile() {
        return (ClusterFile) getContainer();
    }

    public static void setLogger(Logger logger) {
        ClusterFileReadyState.logger = logger;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
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

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }


    private State processGetClusterFileMessage(GetClusterFileMessage getClusterFileMessage) throws MirandaException {
        List<NodeElement> newList = new ArrayList<NodeElement>(getClusterFile().getData());

        ClusterFileMessage clusterFileMessage = new ClusterFileMessage(getClusterFile().getQueue(), this,
                newList, getClusterFile().getVersion());

        getClusterFileMessage.reply(clusterFileMessage);

        return this;
    }

    /**
     * This message means that we should update all the matching nodes time
     * of last connection, and possibly drop the nodes that don't match.  A
     * node that has not connected in an amount of time (in milliseconds)
     * specified by {@link MirandaProperties#PROPERTY_CLUSTER_TIMEOUT}
     * should be dropped.
     *
     * @param healthCheckUpdateMessage
     * @return
     */
    private State processHealthCheckUpdateMessage(HealthCheckUpdateMessage healthCheckUpdateMessage) {
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
        long timeout = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_CLUSTER_TIMEOUT, MirandaProperties.DEFAULT_CLUSTER_TIMEOUT);
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

            getClusterFile().updateVersion();
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
            getClusterFile().updateVersion();
            getClusterFile().write();
        }

        return this;

    }

    public Type getListType() {
        return new TypeToken<List<NodeElement>>() {
        }.getType();
    }


    public void write() {
        WriteMessage writeMessage = new WriteMessage(getClusterFile().getFilename(), getClusterFile().getBytes(), getClusterFile().getQueue(), this);
        send(getClusterFile().getWriterQueue(), writeMessage);
    }



    public boolean contains(Object o) {
        NodeElement nodeElement = (NodeElement) o;
        return getClusterFile().contains(nodeElement);
    }


    @Override
    public Version getVersion() {
        return getClusterFile().getVersion();
    }



    public void add(Object o) {
        NodeElement nodeElement = (NodeElement) o;
        getClusterFile().getData().add(nodeElement);
    }


    @Override
    public SingleFile getFile() {
        return getClusterFile();
    }



    public String getName() {
        return "clusters";
    }


    @Override
    public String toString() {
        return "ReadyState";
    }

    private State processWriteFailedMessage(WriteFailedMessage message) {
        logger.error("Failed to write cluster file: " + message.getFilename(), message.getCause());

        return this;
    }

    public State start() {
        State nextState = super.start();

        MirandaProperties properties = Miranda.properties;

        long healthCheckPeriod = properties.getLongProperty(MirandaProperties.PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD, MirandaProperties.DEFAULT_CLUSTER_HEALTH_CHECK_PERIOD);
        HealthCheckMessage healthCheckMessage = new HealthCheckMessage(getClusterFile().getCluster(), this);
        Miranda.timer.sendSchedulePeriodic(healthCheckPeriod, getClusterFile().getCluster(), healthCheckMessage);

        return nextState;
    }


    private State processNodesUpdatedMessage(NodesUpdatedMessage nodesUpdatedMessage) {
        List<NodeElement> copy = new ArrayList<NodeElement>(nodesUpdatedMessage.getNodeList());
        getClusterFile().setData(copy);
        getClusterFile().write();

        return this;
    }

}
