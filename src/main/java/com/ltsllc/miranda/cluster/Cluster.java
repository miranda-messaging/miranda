package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.network.NodeAddedMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


/**
 * A logical grouping of {@Link Node}.
 * <p>
 * <p>
 * This class allows the rest of the system to treat a cluster like a single unit.
 * For example the system can "tell" the cluster about a new message and
 * let the class worry about distributing it.
 * </P>
 * Created by Clark on 12/31/2016.
 */
public class Cluster extends Consumer {
    private Logger logger = Logger.getLogger(Cluster.class);


    private static Cluster ourInstance;



    public static synchronized void initializeClass(String filename, BlockingQueue<Message> writerQueue, BlockingQueue<Message> network) {
        if (null == ourInstance) {
            ourInstance = new Cluster(filename, writerQueue, network);
        }
    }

    public static Cluster getInstance() {
        return ourInstance;
    }

    private Cluster(String filename, BlockingQueue<Message> writerQueue, BlockingQueue<Message> network) {
        super("Cluster");

        ClusterReadyState readyState = new ClusterReadyState(this);
        setCurrentState(readyState);

        this.writer = writerQueue;
        this.network = network;
        this.clusterFile = new ClusterFile(filename, this.writer);
    }


    private List<Node> nodes = new ArrayList<Node>();
    private BlockingQueue<Message> network;
    private BlockingQueue<Message> writer;
    private ClusterFile clusterFile;


    private NetworkListener networkListener;

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public BlockingQueue<Message> getWriter() {
        return writer;
    }
/*
    public State processMessage(Message m) {
        State nextState = getCurrentState();

        switch (m.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) m;
                nextState = processLoad(loadMessage);
                break;
            }

            case Connect: {
                processConnect();
                break;
            }

            case NodeAdded: {
                NodeAddedMessage nodeAddedMessage = (NodeAddedMessage) m;
                nextState = processNodeAdded(nodeAddedMessage);
                break;
            }

            case NodesLoaded: {
                NodesLoadedMessage nodesLoaded = (NodesLoadedMessage) m;
                nextState = processNodesLoaded(nodesLoaded);
                break;
            }
        }

        return nextState;
    }

*/



    public State start () {
        super.start();

        State state = new ClusterReadyState(this);
        setCurrentState(state);

        clusterFile.start();
        int port = PropertiesUtils.getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        networkListener = new NetworkListener(port);
        networkListener.listen();

        return new ClusterReadyState(this);
    }


    public static void load (String filename) {
        LoadMessage loadMessage = new LoadMessage(getInstance().getQueue(), filename, null);
        getInstance().send(loadMessage, getInstance().getClusterFile().getQueue());
    }

    public void addNode (NodeElement nodeElement) {
        if (!contains(nodeElement)) {
            Node node = new Node(nodeElement, getNetwork());
            node.start();
            node.connect(this.getQueue(), this);
        }
    }

    private boolean contains (NodeElement nodeElement) {
        for (Node node : nodes) {
            if (node.equalsElement(nodeElement))
                return true;
        }

        return false;
    }

    public void addNewNode (Node node) {
        nodes.add(node);

        NewNodeMessage newNodeMessage = new NewNodeMessage(getQueue(), this, node.getDns(), node.getIp(), node.getPort(), node.getDescription());
        send(newNodeMessage, getClusterFile().getQueue());
    }


    public void connect () {
        ConnectMessage connectMessage = new ConnectMessage(getQueue(), this);
        for (Node node : nodes) {
            send(connectMessage, node.getQueue());
        }
    }


    public static void nodeAdded (BlockingQueue<Message> senderQueue, Object sender, String dns, String ip, int port, String desciption) {
        NewNodeMessage newNodeMessage = new NewNodeMessage(senderQueue, sender, dns, ip, port, desciption);
        staticSend(newNodeMessage, getInstance().getQueue());
    }
}
