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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
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


    public static void nodesLoaded(List<NodeElement> data) {
        getInstance().instanceNodesLoaded(data);
    }

    private void instanceNodesLoaded(List<NodeElement> data) {
        Message m = new NodesLoadedMessage(data, getQueue(), this);
        send(m, getQueue());
    }

    private static Cluster ourInstance;

    private String filename;




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

        ReadyState readyState = new ReadyState(this);
        setCurrentState(readyState);

        this.writer = writerQueue;
        this.network = network;
        setCurrentState(new ReadyState(this));
        this.filename = filename;
    }


    private List<Node> nodes = new ArrayList<Node>();
    private BlockingQueue<Message> clusterFile;
    private BlockingQueue<Message> network;
    private BlockingQueue<Message> writer;


    private NetworkListener networkListener;

    public BlockingQueue<Message> getClusterFile() {
        return clusterFile;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getFilename() {
        return filename;
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public BlockingQueue<Message> getWriter() {
        return writer;
    }

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


    private State processLoad (LoadMessage loadMessage) {
        State nextState = getCurrentState();

        send(loadMessage, getClusterFile());

        return nextState;
    }


    private State processNodesLoaded(NodesLoadedMessage nodesLoadedMessage)
    {
        State nextState = getCurrentState();

        for (NodeElement element : nodesLoadedMessage.getNodes()) {
            if (!containsNode(element)) {
                Node n = new Node(element, getNetwork());
                nodes.add(n);
                n.start();
            }
        }

        for (Node n : getNodes()) {
            ConnectMessage connectMessage = new ConnectMessage(getQueue(), this);
            send (connectMessage, n.getQueue());
        }

        return nextState;
    }

    private boolean containsNode (NodeElement element) {
        for (Node n : nodes) {
            if (element.getIp().equals(n.getIp()) && element.getPort() == n.getPort())
                return true;
        }

        return false;
    }


    /**
     * Tell the nodes in the cluster to connect.
     */
    private void processConnect() {
        for (Node n : nodes) {
            ConnectMessage connectMessage = new ConnectMessage(getQueue(), this);
            send(connectMessage, n.getQueue());
        }
    }


    public State start () {
        ClusterFile clusterFile = new ClusterFile(getFilename(), getWriter());
        this.clusterFile = clusterFile.getQueue();

        State state = super.start();
        setCurrentState(state);

        clusterFile.start();
        this.clusterFile = clusterFile.getQueue();
        int port = PropertiesUtils.getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        networkListener = new NetworkListener(port);
        networkListener.listen();

        return new ReadyState(this);
    }


    public static void load () {
        LoadMessage loadMessage = new LoadMessage(getInstance().getQueue(), getInstance().getFilename(), null);
        getInstance().send(loadMessage, getInstance().getClusterFile());
    }


    private State processNodeAdded(NodeAddedMessage nodeAddedMessage)
    {
        getNodes().add(nodeAddedMessage.getNode());
        return getCurrentState();
    }
}
