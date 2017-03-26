package com.ltsllc.miranda.cluster;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.cluster.messages.ClusterFileChangedMessage;
import com.ltsllc.miranda.cluster.states.ClusterFileReadyState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteMessage;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
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

    public static synchronized void initialize(String filename, Writer writer, BlockingQueue<Message> cluster) {
        if (null == ourInstance) {
            ourInstance = new ClusterFile(filename, writer, cluster);
            ourInstance.start();
        }
    }

    public static void reset () {
        ourInstance = null;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public ClusterFile (String filename, Writer writer, BlockingQueue<Message> cluster) {
        super(filename, writer);

        this.cluster = cluster;

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(this);
        setCurrentState(clusterFileReadyState);
    }

    public ClusterFile (String filename, Writer writer, BlockingQueue<Message> queue, List<NodeElement> nodeElementList) {
        super(filename, writer);

        this.cluster = queue;

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(this);
        setCurrentState(clusterFileReadyState);

        setData(nodeElementList);
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
        boolean dirty = false;

        if (!containsElement(nodeElement)) {
            getData().add(nodeElement);

            byte[] buffer = getBytes();

            dirty = true;
        }

        if (dirty)
            write();
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

            try {
                updateVersion();
            } catch (NoSuchAlgorithmException e) {
                Panic panic = new Panic ("Exception trying to calculate version", e, Panic.Reasons.ExceptionTryingToCalculateVersion);
                Miranda.getInstance().panic(panic);
            }

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

        try {
            updateVersion();
        } catch (NoSuchAlgorithmException e) {
            Panic panic = new Panic("Exception calculating new version", e, Panic.Reasons.ExceptionTryingToCalculateVersion);
            Miranda.getInstance().panic(panic);
        }

        write();
    }
}
