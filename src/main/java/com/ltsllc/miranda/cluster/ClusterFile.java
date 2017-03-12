package com.ltsllc.miranda.cluster;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.messages.ClusterFileChangedMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/20/2017.
 */
public class ClusterFile extends SingleFile<NodeElement> {
    private static Logger logger = Logger.getLogger(ClusterFile.class);
    private static ClusterFile ourInstance;

    private BlockingQueue<Message> cluster;

    public static ClusterFile getInstance () {
        return ourInstance;
    }

    public static synchronized void initialize(String filename, BlockingQueue<Message> writerQueue, BlockingQueue<Message> cluster) {
        if (null == ourInstance) {
            ourInstance = new ClusterFile(filename, writerQueue, cluster);
            ourInstance.start();
        }
    }

    public static void reset () {
        ourInstance = null;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    private ClusterFile (String filename, BlockingQueue<Message> writer, BlockingQueue<Message> cluster) {
        super(filename, writer);

        this.cluster = cluster;

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(this, getCluster());
        setCurrentState(clusterFileReadyState);
    }

    public void addNode(Node node) {
        if (!containsNode(node)) {
            NodeElement nodeElement = new NodeElement(node);
            getData().add(nodeElement);

            byte[] buffer = getBytes();
            WriteMessage writeMessage = new WriteMessage(getFilename(), buffer, getQueue(), this);
            send(writeMessage, getWriterQueue());
        }
    }


    public void addNode (NodeElement nodeElement) {
        if (!containsElement(nodeElement)) {
            getData().add(nodeElement);

            byte[] buffer = getBytes();
            WriteMessage writeMessage = new WriteMessage(getFilename(), buffer, getQueue(), this);
            send(writeMessage, getWriterQueue());
        }
    }


    private boolean containsNode(Node node) {
        for (NodeElement nodeElement : getData()) {
            if (nodeElement.getDns().equals(node.getDns()) && nodeElement.getIp().equals(node.getIp()) && nodeElement.getPort() == node.getPort())
                return true;
        }
        return false;
    }


    private boolean containsElement (NodeElement nodeElement) {
        if (null == getData())
        {
            logger.error("null data");
            setData(new ArrayList<NodeElement>());
        }

        for (NodeElement element : getData()) {
            if (element.equals(nodeElement))
                return true;
        }

        return false;
    }


    public List buildEmptyList () {
        return new ArrayList<NodeElement>();
    }

    public Type listType () {
        return new TypeToken<ArrayList<NodeElement>>(){}.getType();
    }


    public boolean contains (NodeElement nodeElement) {
        for (NodeElement element : getData()) {
            if (element.equals(nodeElement))
                return true;
        }

        return false;
    }


    public void merge (List<NodeElement> list) {
        List<NodeElement> adds = new ArrayList<NodeElement>();

        for (NodeElement element : list) {
            if (!containsElement(element)) {
                adds.add(element);
            }
        }

        if (adds.size() > 0) {
            getData().addAll(adds);
            updateVersion();

            WriteMessage writeMessage = new WriteMessage(getFilename(), getBytes(), getQueue(), this);
            send(writeMessage, getWriterQueue());

            ClusterFileChangedMessage clusterFileChangedMessage = new ClusterFileChangedMessage(getQueue(), this, getData(), getVersion());
            send(clusterFileChangedMessage, getCluster());
        }
    }


    public void updateNode (NodeElement nodeElement) {
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
    public NodeElement matchingNode (NodeElement nodeElement) {
        for (NodeElement element : getData())
            if (element.equals(nodeElement))
                return element;

        return null;
    }


    public void updateNode (NodeElement oldValue, NodeElement newValue) {
        if (!contains(oldValue)) {
            logger.error ("asked to update a node that we don't contain");
            return;
        }

        NodeElement current = matchingNode(oldValue);
        current.update(newValue);

        updateVersion();
        write();
    }
}
