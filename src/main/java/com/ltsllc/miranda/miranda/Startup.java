package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.cluster.messages.HealthCheckMessage;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.file.*;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.server.HttpServer;
import com.ltsllc.miranda.server.NewTopicHandler;
import com.ltsllc.miranda.subsciptions.NewSubscriptionHandler;
import com.ltsllc.miranda.subsciptions.SubscriptionsFile;
import com.ltsllc.miranda.timer.SchedulePeriodicMessage;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.user.NewUserHandler;
import com.ltsllc.miranda.user.UsersFile;
import com.ltsllc.miranda.util.PropertiesUtils;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ltsllc.miranda.file.MirandaProperties.*;

/**
 * A class that encapsulates the knowledge of how to start up the system.
 * <p>
 * While a startup is in progress the system will not accept connections.
 * Startup tries to read the system properties
 * file the tries to read the users, topics, subscriptions, messages,
 * deliveries and nodes files.
 * <p>
 * If a problem is encountered reading the systems properties file the system panics and shuts down.
 * In all other situations the object will try to read as much of the file as possible.
 * After that, the system tries to connect to the other nodes in the cluster and synchronize with them.
 * <p>
 * Created by Clark on 12/30/2016.
 */
public class Startup extends State {
    private enum LogLevel {
        NORMAL,
        DEBUG,
        WARN,
        ERROR,
        INFO
    }

    public static final String DEBUG_FLAG = "-debug";

    public static final String LOG4J_FILE = "log4j.xml";

    private static Logger logger;

    private String[] arguments = {};
    private BlockingQueue<Message> clusterQueue;
    private BlockingQueue<Message> writerQueue;
    private int index;
    private LogLevel logLevel = LogLevel.NORMAL;
    private String propertiesFilename = DEFAULT_PROPERTIES_FILENAME;
    private boolean debugMode = false;
    private HttpServer httpServer;


    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public boolean debugMode () {
        return getDebugMode();
    }

    public boolean getDebugMode () {
        return  debugMode;
    }

    public void setDebugMode (boolean debugMode) {
        this.debugMode = debugMode;
    }

    public String getPropertiesFilename() {
        return propertiesFilename;
    }

    public void setPropertiesFilename(String propertiesFilename) {
        this.propertiesFilename = propertiesFilename;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public BlockingQueue<Message> getClusterQueue() {
        return clusterQueue;
    }

    public void setClusterQueue(BlockingQueue<Message> queue) {
        clusterQueue = queue;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public Startup(Consumer container) {
        super(container);
    }

    public Startup(String[] argv) {
        super(null);
        arguments = argv;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject())  {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage (garbageCollectionMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] argv) {
        arguments = argv;
    }

    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public void setWriterQueue (BlockingQueue<Message> writerQueue) {
        this.writerQueue = writerQueue;
    }


    public State start() {
        parseCommandLine();
        startLogger();
        loadProperties();
        startSubsystems();
        loadFiles();
        setRootUser();
        schedule();
        startHttpServices();
        Miranda.performGarbageCollection();
        return new ReadyState(Miranda.getInstance());
    }

    private void setRootUser() {
        User root = new User("root", "System admin");

        if (debugMode())
        {
            root = new User("root", "System admin");
        }

        UsersFile.getInstance().add(root, false);
    }

    private void startLogger() {
        Properties p = System.getProperties();
        String filename = p.getProperty(PROPERTY_LOG4J_FILE, DEFAULT_LOG4J_FILE);
        DOMConfigurator.configure(filename);

        logger = Logger.getLogger(Startup.class);

        switch (getLogLevel()) {
            case DEBUG:
                logger.setLevel(Level.DEBUG);
                break;

            case ERROR:
                logger.setLevel(Level.ERROR);
                break;

            case INFO:
                logger.setLevel(Level.INFO);
                break;

            case NORMAL:
                logger.setLevel(Level.ERROR);
                break;

            case WARN:
                logger.setLevel(Level.WARN);
                break;
        }

    }

    public void parseCommandLine() {
        for (String s : getArguments()) {
            if (s.equals(DEBUG_FLAG)) {
                setLogLevel(LogLevel.DEBUG);
                setDebugMode(true);
                setIndex(getIndex() + 1);
            }
        }

        if (getIndex() < getArguments().length) {
            setPropertiesFilename(getArguments()[getIndex()]);
            incrementIndex();
        }
    }


    /**
     * Load the system properties; using the defaults if nothing else is available.
     * <p>
     * The method will use the first argument passed to it, if supplied,
     * otherwise it will use {@link MirandaProperties#PROPERTY_SYSTEM_PROPERTIES if that is defined.
     * If neither is defined then it
     * will look for {@link MirandaProperties#DEFAULT_PROPERTIES_FILENAME} in the current directory.
     * <p>
     * <p>
     * The method always "augments" the system properties with the {@link #DEFAULT_PROPERTIES}
     * if it finds a proerties file it will augment the result with that
     * file.
     */
    private void loadProperties() {
        MirandaProperties.initialize(getPropertiesFilename());
        PropertiesUtils.log(System.getProperties());
    }



    /**
     * Start the Miranda subsystems.
     * <p>
     * <p>
     * This method starts the {@link Writer}, the {@link Cluster} and the
     * {@link Network} object.  The Cluster may start additional subsystems
     * for the Nodes in the Cluster.
     * </P>
     */
    private void startSubsystems() {
        MirandaProperties properties = MirandaProperties.getInstance();
        Miranda miranda = Miranda.getInstance();

        StartState.initialize();

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        setWriterQueue(queue);

        Writer w = new Writer(queue);
        w.start();

        BlockingQueue<Message> networkQueue = new LinkedBlockingQueue<Message>();
        Network network = new Network(networkQueue);
        network.start();

        queue = new LinkedBlockingQueue<Message>();
        String filename = System.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

        Cluster.initializeClass(filename, getWriterQueue(), queue);
        Cluster.getInstance().start();
        Cluster.getInstance().connect();

        int port = properties.getIntegerProperty(MirandaProperties.PROPERTY_PORT);
        HttpServer httpServer = new HttpServer(port);
        httpServer.startup();
        setHttpServer(httpServer);
        miranda.setHttpServer(httpServer);
    }


    private void startHttpServices() {
        HttpServer httpServer = Miranda.getInstance().getHttpServer();

        NewUserHandler newUserHandler = new NewUserHandler(UsersFile.getInstance());
        newUserHandler.start();
        getHttpServer().registerPostHandler("/users", newUserHandler.getQueue());

        NewTopicHandler newTopicHandler = new NewTopicHandler(TopicsFile.getInstance());
        newTopicHandler.start();
        httpServer.registerPostHandler("/topics", newTopicHandler.getQueue());

        NewSubscriptionHandler newSubscriptionHandler = new NewSubscriptionHandler(SubscriptionsFile.getInstance());
        newSubscriptionHandler.start();
        httpServer.registerPostHandler("/subscriptions", newSubscriptionHandler.getQueue());
    }

    private void loadFiles() {
        try {
            String clusterFile = System.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

            ClusterFile.initialize(clusterFile, getWriterQueue(), Cluster.getInstance().getQueue());

            String filename = System.getProperty(MirandaProperties.PROPERTY_USERS_FILE);
            UsersFile.initialize(filename, getWriterQueue());

            filename = System.getProperty(MirandaProperties.PROPERTY_TOPICS_FILE);
            TopicsFile.initialize(filename, getWriterQueue());

            filename = System.getProperty(MirandaProperties.PROPERTY_SUBSCRIPTIONS_FILE);
            SubscriptionsFile.initialize(filename, getWriterQueue());

            String directoryName = System.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
            File f = new File(directoryName);
            directoryName = f.getCanonicalPath();
            SystemMessages messages = new SystemMessages(directoryName, getWriterQueue());
            messages.start();
            messages.load();
            messages.updateVersion();
            Miranda miranda = Miranda.getInstance();
            miranda.setSystemMessages(messages);

            directoryName = System.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
            f = new File(directoryName);
            directoryName = f.getCanonicalPath();
            SystemDeliveriesFile deliveriesFile = new SystemDeliveriesFile(directoryName, getWriterQueue());
            deliveriesFile.start();
            deliveriesFile.load();
            deliveriesFile.updateVersion();
            miranda.setDeliveriesFile(deliveriesFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    public void schedule () {
        MirandaProperties properties = MirandaProperties.getInstance();

        long healthCheckPeriod = properties.getLongProperty(MirandaProperties.PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD);
        HealthCheckMessage healthCheckMessage = new HealthCheckMessage(Miranda.getInstance().getQueue(), this);
        SchedulePeriodicMessage scheduleMessage = new SchedulePeriodicMessage(Miranda.getInstance().getQueue(), this, healthCheckMessage, healthCheckPeriod);
        Consumer.staticSend(scheduleMessage, Miranda.timer.getQueue());

        long garbageCollectionPeriod = properties.getLongProperty(MirandaProperties.PROPERTY_GARBAGE_COLLECTION_PERIOD);

        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(Miranda.getInstance().getQueue(),
                Miranda.timer);
        scheduleMessage = new SchedulePeriodicMessage(Miranda.getInstance().getQueue(), this,
                garbageCollectionMessage, garbageCollectionPeriod);
        Consumer.staticSend(scheduleMessage, Miranda.timer.getQueue());

        Miranda.performGarbageCollection();
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        send(ClusterFile.getInstance().getQueue(), garbageCollectionMessage);
        send(UsersFile.getInstance().getQueue(), garbageCollectionMessage);
        send(TopicsFile.getInstance().getQueue(), garbageCollectionMessage);
        send(SubscriptionsFile.getInstance().getQueue(), garbageCollectionMessage);

        Miranda miranda = Miranda.getInstance();
        send(miranda.getSystemMessages().getQueue(), garbageCollectionMessage);
        send(miranda.getDeliveriesFile().getQueue(), garbageCollectionMessage);

        return this;
    }


    public void incrementIndex () {
        int index = getIndex();
        index++;
        setIndex(index);
    }
}
