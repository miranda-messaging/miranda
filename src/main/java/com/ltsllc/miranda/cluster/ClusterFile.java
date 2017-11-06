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

package com.ltsllc.miranda.cluster;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.MergeException;
import com.ltsllc.miranda.clientinterface.basicclasses.MirandaObject;
import com.ltsllc.miranda.clientinterface.basicclasses.NodeElement;
import com.ltsllc.miranda.cluster.messages.ClusterFileChangedMessage;
import com.ltsllc.miranda.cluster.messages.ConnectMessage;
import com.ltsllc.miranda.cluster.states.ClusterFileReadyState;
import com.ltsllc.miranda.cluster.states.ClusterFileStartingState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.WriteMessage;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/20/2017.
 */
public class ClusterFile extends SingleFile<NodeElement> {
    public static final String NAME = "clusterfile";

    private static Logger logger = Logger.getLogger(ClusterFile.class);
    private static ClusterFile ourInstance;

    private BlockingQueue<Message> cluster;

    public static ClusterFile getInstance() {
        return ourInstance;
    }

    public static synchronized void initialize(String filename, Reader reader, Writer writer,
                                               BlockingQueue<Message> cluster) throws IOException, MirandaException {
        if (null == ourInstance) {
            ourInstance = new ClusterFile(filename, reader, writer, cluster);
            ourInstance.start();
        }
    }

    public static void reset() {
        ourInstance = null;
    }

    public static void setLogger(Logger logger) {
        ClusterFile.logger = logger;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public ClusterFile(String filename, Reader reader, Writer writer, BlockingQueue<Message> cluster) throws IOException, MirandaException {
        basicConstructor (filename, reader, writer, cluster);
    }

    public ClusterFile(String filename, Reader reader, Writer writer, BlockingQueue<Message> queue,
                       List<NodeElement> nodeElementList) throws IOException, MirandaException {
        super(filename, reader, writer);

        this.cluster = queue;

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(this);
        setCurrentState(clusterFileReadyState);

        setData(nodeElementList);
    }

    public void basicConstructor (String filename, Reader reader, Writer writer, BlockingQueue<Message> cluster)
            throws IOException, MirandaException {
        super.basicConstructor(filename, reader, writer);

        this.cluster = cluster;

        ClusterFileStartingState clusterFileStartingState = new ClusterFileStartingState(this);
        setCurrentState(clusterFileStartingState);
    }

    public void addNode(NodeElement nodeElement) {
        if (!containsElement(nodeElement)) {
            getData().add(nodeElement);

            byte[] buffer = getBytes();
            write();
        }
    }


    public boolean containsElement(NodeElement nodeElement) {
        if (null == getData()) {
            logger.error("null data");
            setData(new ArrayList<NodeElement>());
        }

        for (NodeElement element : getData()) {
            if (element.equals(nodeElement))
                return true;
        }

        return false;
    }


    public List buildEmptyList() {
        return new ArrayList<NodeElement>();
    }

    public Type getListType() {
        return new TypeToken<ArrayList<NodeElement>>() {
        }.getType();
    }


    public boolean contains(NodeElement nodeElement) {
        for (NodeElement element : getData()) {
            if (element.equals(nodeElement))
                return true;
        }

        return false;
    }


    public void updateNode(NodeElement nodeElement) {
        for (NodeElement element : getData()) {
            if (element.equals(nodeElement)) {
                element.setLastConnected(nodeElement.getLastConnected());
            }
        }
    }


    /**
     * Return the {@link NodeElement} that {@link #equals(Object)} the value passed into us;
     * otherwise return null.
     *
     * @param nodeElement
     * @return
     */
    public NodeElement matchingNode(NodeElement nodeElement) {
        for (NodeElement element : getData())
            if (element.equals(nodeElement))
                return element;

        return null;
    }


    public void checkForDuplicates() {
        List<NodeElement> duplicates = new ArrayList<NodeElement>();

        for (NodeElement current : getData()) {
            for (NodeElement nodeElement : getData()) {
                if (current.equivalent(nodeElement) && current != nodeElement) {
                    duplicates.add(current);
                    logger.warn(nodeElement.getDns() + " is duplicated");
                }
            }
        }

        getData().removeAll(duplicates);
    }
}
