package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.timer.MirandaTimer;
import org.apache.log4j.Logger;

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
    private BlockingQueue<Message> events;
    private BlockingQueue<Message> deliveries;
    private BlockingQueue<Message> users;
    private BlockingQueue<Message> topics;
    private BlockingQueue<Message> subscriptions;
    private BlockingQueue<Message> cluster;
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

    public BlockingQueue<Message> getEvents() {
        return events;
    }

    public BlockingQueue<Message> getDeliveries() {
        return deliveries;
    }

    public void setDeliveries(BlockingQueue<Message> deliveries) {
        this.deliveries = deliveries;
    }

    public BlockingQueue<Message> getTopics() {
        return topics;
    }

    public void setTopics(BlockingQueue<Message> topics) {
        this.topics = topics;
    }

    public BlockingQueue<Message> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(BlockingQueue<Message> subscriptions) {
        this.subscriptions = subscriptions;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public void setCluster(BlockingQueue<Message> cluster) {
        this.cluster = cluster;
    }

    public BlockingQueue<Message> getUsers() {
        return users;
    }

    public void setUsers(BlockingQueue<Message> users) {
        this.users = users;
    }

    public void setEvents(BlockingQueue<Message> events) {
        this.events = events;
    }

    public Miranda (String[] argv) {
        super ("miranda");

        ourInstance = this;

        State s = new Startup(this, argv);
        setCurrentStateWithoutStart(s);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
    }

    public synchronized static Miranda getInstance() {
        return ourInstance;
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

        sendIfNotNull (message, getUsers());
        sendIfNotNull (message, getTopics());
        sendIfNotNull (message, getSubscriptions());
        sendIfNotNull (message, getEvents());
        sendIfNotNull (message, getDeliveries());
    }
}
