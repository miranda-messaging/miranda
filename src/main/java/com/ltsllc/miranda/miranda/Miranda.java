package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.property.NewPropertiesMessage;
import com.ltsllc.miranda.servlet.*;
import com.ltsllc.miranda.servlet.objects.Property;
import com.ltsllc.miranda.servlet.objects.StatusObject;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.UsersFile;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Miranda system.
 *
 * This is also the main class for the system.
 *
 */
public class Miranda extends Consumer {
    private static Logger logger = Logger.getLogger(Miranda.class);
    private static Miranda ourInstance;

    public static FileWatcherService fileWatcher;
    public static MirandaTimer timer;
    public static MirandaProperties properties;
    public static MirandaCommandLine commandLine;
    public static MirandaFactory factory;
    public static boolean panicking = false;

    private BlockingQueue<Message> httpServer;
    private UsersFile users;
    private TopicsFile topics;
    private SubscriptionsFile subscriptions;
    private SystemMessages events;
    private SystemDeliveriesFile deliveries;
    private Cluster cluster;
    private PanicPolicy panicPolicy;

    public PanicPolicy getPanicPolicy() {
        return panicPolicy;
    }

    public void setPanicPolicy(PanicPolicy panicPolicy) {
        this.panicPolicy = panicPolicy;
    }

    public static Logger getLogger () {
        return logger;
    }

    public BlockingQueue<Message> getHttp() {
        return httpServer;
    }

    public void setHttp(BlockingQueue<Message> httpServer) {
        this.httpServer = httpServer;
    }

    public SystemDeliveriesFile getDeliveries () {
        return deliveries;
    }

    public void setDeliveries(SystemDeliveriesFile deliveries) {
        this.deliveries = deliveries;
    }

    public SystemMessages getEvents () {
        return events;
    }

    public void setEvents(SystemMessages events) {
        this.events = events;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public UsersFile getUsers() {
        return users;
    }

    public void setUsers(UsersFile users) {
        this.users = users;
    }

    public TopicsFile getTopics() {
        return topics;
    }

    public void setTopics(TopicsFile topics) {
        this.topics = topics;
    }

    public SubscriptionsFile getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(SubscriptionsFile subscriptions) {
        this.subscriptions = subscriptions;
    }

    public Miranda (String[] argv) {
        super ("miranda");

        ourInstance = this;

        State s = new Startup(this, argv);
        setCurrentStateWithoutStart(s);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
    }

    public static Miranda getInstance() {
        return ourInstance;
    }

    public synchronized static void setInstance (Miranda miranda) {
        ourInstance = miranda;
    }

    /**
     * Something Very Bad happend and part of the system wants to shutdown.
     * Return true if we agree, which means shutting down the system and false
     * if we want to try and keep going.
     *
     * @param panic
     * @return
     */
    public boolean panic (Panic panic) {
        boolean continuePanic = getPanicPolicy().panic(panic);
        panicking = continuePanic;
        return continuePanic;
    }

    public static void main(String[] argv) {
        logger.info ("Starting");
        Miranda miranda = new Miranda(argv);
        miranda.start();
    }

    public void reset () {
        fileWatcher = null;
        properties = null;
        timer = null;
        logger = null;

        httpServer = null;
        users = null;
        topics = null;
        subscriptions = null;
        events = null;
        deliveries = null;
        panicPolicy = null;
    }


    public void performGarbageCollection() {
        GarbageCollectionMessage message = new GarbageCollectionMessage(null, this);
        send(message, getQueue());
    }

    public void stop () {
        StopState stop = StopState.getInstance();
        setCurrentState(stop);

        StopMessage message = new StopMessage(getQueue(), this);

        sendIfNotNull (message, getCluster());
        sendIfNotNull (message, getHttp());

        sendIfNotNull(message, fileWatcher);
        sendIfNotNull(message, properties);
        sendIfNotNull(message, timer);

        if (null != getCluster()) {
            getCluster().sendStop(getQueue(), this);
        }

        if (null != getUsers()) {
            getUsers().sendStop(getQueue(), this);
        }

        if (null != getTopics()) {
            getTopics().sendStop(getQueue(), this);
        }

        if (null != getSubscriptions()) {
            getSubscriptions().sendStop(getQueue(), this);
        }
    }

    public void getStatus (BlockingQueue<Message> respondTo) {
        GetStatusMessage getStatusMessage = new GetStatusMessage(respondTo, this);
        send(getStatusMessage, getQueue());
    }

    public StatusObject getStatusImpl () {
        MirandaProperties properties = Miranda.properties;

        String localDns = properties.getProperty(MirandaProperties.PROPERTY_MY_DNS);
        String localIp = properties.getProperty(MirandaProperties.PROPERTY_MY_IP);
        int localPort = properties.getIntProperty(MirandaProperties.PROPERTY_MY_PORT);
        String localDescription = properties.getProperty(MirandaProperties.PROPERTY_MY_DESCIPTION);

        NodeElement local = new NodeElement(localDns, localIp, localPort, localDescription);
        List<Property> list = properties.asPropertyList();

        StatusObject statusObject = new StatusObject(local, list, null);

        return statusObject;
    }

    public void sendNewProperties (BlockingQueue<Message> senderQueue, Object sender, MirandaProperties mirandaProperties) {
        NewPropertiesMessage newPropertiesMessage = new NewPropertiesMessage(senderQueue, sender, mirandaProperties);
        sendToMe(newPropertiesMessage);
    }

    public void shutdown () {
        if (null != getCluster())
            getCluster().sendShutdown(getQueue(), this);

        if (null != getUsers())
            getUsers().sendShutdown(getQueue(), this);

        if (null != getTopics())
            getTopics().sendShutdown(getQueue(), this);

        if (null != getSubscriptions())
            getSubscriptions().sendShutdown(getQueue(), this);

        if (null != getEvents())
            getEvents().sendShutdown(getQueue(), this);

        if (null != getDeliveries())
            getDeliveries().sendShutdown(getQueue(), this);

        if (null != fileWatcher)
            fileWatcher.sendShutdown(getQueue(), this);

        if (null != timer)
            timer.sendShutdown(getQueue(), this);

        if (null != properties)
            properties.sendShutdown(getQueue(), this);

        setCurrentState(new ShuttingDownState(this));
    }
}
