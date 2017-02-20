package com.ltsllc.miranda.cluster;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Utils;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.util.IOUtils;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    private Version version;

    public static ClusterFile getInstance () {
        return ourInstance;
    }

    public static synchronized void initialize(String filename, BlockingQueue<Message> writerQueue, BlockingQueue<Message> cluster) {
        if (null == ourInstance) {
            ourInstance = new ClusterFile(filename, writerQueue, cluster);
            ourInstance.load();
            ourInstance.start();
        }
    }


    public void setVersion(Version version) {
        this.version = version;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    private ClusterFile (String filename, BlockingQueue<Message> writer, BlockingQueue<Message> cluster) {
        super(filename, writer);

        ClusterFileReadyState clusterFileReadyState = new ClusterFileReadyState(this);
        setCurrentState(clusterFileReadyState);

        this.cluster = cluster;
    }

    public Version getVersion() {
        return version;
    }

    public State processMessage (Message message) {
        State nextState = getCurrentState();

        switch (message.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) message;
                nextState = processLoadMessage(loadMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processLoadMessage (LoadMessage loadMessage) {
        State nextState = getCurrentState();

        load();

        NodesLoadedMessage nodesLoadedMessage = new NodesLoadedMessage(getData(), getQueue(), this);
        send(nodesLoadedMessage, getCluster());

        return nextState;
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
        boolean changed = false;

        for (NodeElement element : list) {
            if (!containsElement(element)) {
                getData().add(element);
                changed = true;
            }
        }

        if (changed) {
            String sha1 = Utils.calculateSha1(getBytes());
            Version version = new Version(sha1);
            setVersion(version);
            ClusterFileChangedMessage clusterFileChangedMessage = new ClusterFileChangedMessage(getQueue(), this, getData(), version);
            send(clusterFileChangedMessage, Cluster.getInstance().getQueue());

            WriteMessage writeMessage = new WriteMessage(getFilename(), getBytes(), getQueue(), this);
            send(writeMessage, getWriterQueue());
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
}
