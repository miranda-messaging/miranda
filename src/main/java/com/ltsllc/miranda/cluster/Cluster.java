package com.ltsllc.miranda.cluster;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.network.InboundNodeHandler;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.ServerConnectedState;
import com.ltsllc.miranda.util.PropertiesUtils;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
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

/*
    public static class NodeChannelInitializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;
        private Cluster cluster;

        public NodeChannelInitializer (Cluster cluster, SslContext sslContext) {
            this.cluster = cluster;
            this.sslContext = sslContext;
        }

        public Cluster getCluster() {
            return cluster;
        }

        public void initChannel (SocketChannel sc) {
            SslHandler sslHandler = sslContext.newHandler(sc.alloc());
            SSLEngine sslEngine = sslContext.newEngine(sc.alloc());
            sc.pipeline().addLast(sslHandler);

            Node n = new Node(sc.remoteAddress());
            n.setCurrentState(new ServerConnectedState(n));
            n.start();

            InboundNodeHandler inboundNodeHandler = new InboundNodeHandler(getCluster().getNetwork(), n.getQueue());
            sc.pipeline().addLast(inboundNodeHandler);
        }
    }
    */

    public static void nodesLoaded(List<NodeElement> data) {
        getInstance().instanceNodesLoaded(data);
    }

    private void instanceNodesLoaded(List<NodeElement> data) {
        Message m = new NodesLoaded(data, getQueue());
        send(m, getQueue());
    }

    private class ClusterIntializer extends ChannelInitializer<SocketChannel> {
        public void initChannel(SocketChannel ch) throws Exception {
            ch.pipeline().addLast(new NodeHandler());
        }
    }

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
        this.network = network;
        setCurrentState(new ReadyState(this));
        ClusterFile file = new ClusterFile(filename, writerQueue);
        setClusterFile(file);
    }


    private List<Node> nodes = new ArrayList<Node>();
    private ClusterFile clusterFile;
    private BlockingQueue<Message> network;
    private NetworkListener networkListener;

    public ClusterFile getClusterFile() {
        return clusterFile;
    }

    public void setClusterFile(ClusterFile clusterFile) {
        this.clusterFile = clusterFile;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public State processMessage(Message m) {
        switch (m.getSubject()) {
            case Connect: {
                processConnect();
                break;
            }

            case NodesLoaded: {
                NodesLoaded nodesLoaded = (NodesLoaded) m;
                List<NodeElement> l = nodesLoaded.getNodes();
                processNodesLoaded (l);
                break;
            }
        }

        return getCurrentState();
    }

    private void processNodesLoaded(List<NodeElement> l) {
        for (NodeElement element : l) {
            if (!containsNode(element)) {
                Node n = new Node(element, getNetwork());
                nodes.add(n);
                n.start();
            }
        }

        for (Node n : getNodes()) {
            ConnectMessage connectMessage = new ConnectMessage(getQueue());
            send (connectMessage, n.getQueue());
        }
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
            ConnectMessage connectMessage = new ConnectMessage(getQueue());
            send(connectMessage, n.getQueue());
        }
    }

    public void newMessages(Message[] a) {

    }


    public void run() {
        NetworkListener networkListener = new NetworkListener(6789);
        networkListener.listen();
    }


    private X509Certificate loadCertificate() {
        X509Certificate certificate = null;

        try {
            String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);

            KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
            FileInputStream fin = new FileInputStream(keyStoreFilename);
            ks.load(fin, passwordString.toCharArray());

            certificate = (X509Certificate) ks.getCertificate("ca");
        } catch (Exception e) {
            logger.fatal ("exception during test", e);
            System.exit(1);
        }

        return certificate;
    }


    public SslContext createChildContext () {
        SslContext sslContext = null;

        try {
            PrivateKey privateKey = loadKey();
            X509Certificate certificate = loadCertificate();

            String[] cipherSuites = SSLContext.getDefault().getSocketFactory().getSupportedCipherSuites();
            List<String> ciphers = Arrays.asList(cipherSuites);

            sslContext = SslContextBuilder
                    .forClient()
                    .ciphers(ciphers)
                    .build();

        } catch (Exception e) {
            logger.fatal("Exception trying to setup SSL", e);
            System.exit(1);
        }

        return sslContext;
    }

    private PrivateKey loadKey () {
        PrivateKey privateKey = null;

        try {
            String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);

            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fin = new FileInputStream(keyStoreFilename);
            ks.load(fin, passwordString.toCharArray());

            Key k = ks.getKey("server", passwordString.toCharArray());
            privateKey = (PrivateKey) k;
        } catch (Exception e) {
            logger.fatal ("Exception trying to load SSL", e);
            System.exit(1);
        }

        return privateKey;
    }


    public void start () {
        getClusterFile().load();
        super.start();
        int port = PropertiesUtils.getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        networkListener = new NetworkListener(port);
        networkListener.listen();
    }


}
