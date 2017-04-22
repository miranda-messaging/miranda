package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.servlet.*;
import com.ltsllc.miranda.http.SetupServletsMessage;
import com.ltsllc.miranda.servlet.enctypt.CreateKeyPairServlet;
import com.ltsllc.miranda.servlet.file.FileServlet;
import com.ltsllc.miranda.servlet.holder.*;
import com.ltsllc.miranda.servlet.login.LoginHolder;
import com.ltsllc.miranda.servlet.login.LoginServlet;
import com.ltsllc.miranda.servlet.objects.MirandaStatus;
import com.ltsllc.miranda.servlet.objects.ServletMapping;
import com.ltsllc.miranda.servlet.topic.CreateTopicServlet;
import com.ltsllc.miranda.servlet.topic.GetTopicServlet;
import com.ltsllc.miranda.servlet.topic.TopicsServlet;
import com.ltsllc.miranda.servlet.topic.UpdateTopicServlet;
import com.ltsllc.miranda.servlet.user.*;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.socket.SocketNetwork;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

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

    private static Logger logger;

    private String[] arguments = {};
    private int index;
    private LogLevel logLevel = LogLevel.NORMAL;
    private HttpServer httpServer;
    private MirandaCommandLine commandLine;
    private MirandaFactory factory;


    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {
        index = i;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public MirandaCommandLine getCommandLine() {
        return commandLine;
    }

    public Miranda getMiranda () {
        return (Miranda) getContainer();
    }

    public Startup (Miranda miranda, String[] argv){
        super(miranda);

        this.commandLine = new MirandaCommandLine(argv);
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] argv) {
        arguments = argv;
    }

    public BlockingQueue<Message> getWriterQueue() {
        return getMiranda().getWriter().getQueue();
    }

    public Writer getWriter() {
        return getMiranda().getWriter();
    }

    public MirandaFactory getFactory() {
        return factory;
    }

    public void setFactory(MirandaFactory factory) {
        this.factory = factory;
    }

    public State start() {
        super.start();

        try {
            processCommandLine();
            startLogger();
            startWriter();
            loadProperties();
            defineFactory();
            definePanicPolicy();
            startServices();
            startSubsystems();
            loadFiles();
            setupSchedule();
            setupHttpServer();
            setupServlets();
            startHttpServer();
            startListening();
            getMiranda().performGarbageCollection();
            return new ReadyState(getMiranda());
        } catch (MirandaException e) {
            Panic panic = new StartupPanic("Exception during startup", e, StartupPanic.StartupReasons.StartupFailed);
            Miranda.getInstance().panic (panic);
        } catch (Exception e) {
            Panic panic = new StartupPanic("Unchecked exception during startup", e, StartupPanic.StartupReasons.UncheckedException);
            Miranda.getInstance().panic (panic);
        }

        return StopState.getInstance();
    }

    public ServletMapping[] convertToArray (List<ServletMapping> mappings) {
        ServletMapping[] mappingArray = new ServletMapping[mappings.size()];
        for (int i = 0; i < mappingArray.length; i++) {
            mappingArray[i] = mappings.get(i);
        }

        return mappingArray;
    }

    public void setupServlets () {
        List<ServletMapping> mappings = new ArrayList<ServletMapping>();

        ServletMapping servletMapping = new ServletMapping("/servlets/status", StatusServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/properties", PropertiesServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/setProperty", SetPropertyServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/clusterStatus", ClusterStatusServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/login", LoginServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getUsers", GetUsersServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getUser", GetUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/updateUser", UpdateUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/deleteUser", DeleteUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/createKeyPair", CreateKeyPairServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/createUser", CreateUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getTopics", TopicsServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getTopic", GetTopicServlet.class);
        mappings.add (servletMapping);

        servletMapping = new ServletMapping("/servlets/createTopic", CreateTopicServlet.class);
        mappings.add (servletMapping);

        servletMapping = new ServletMapping( "/servlets/updateTopic", UpdateTopicServlet.class);
        mappings.add (servletMapping);

        servletMapping = new ServletMapping("/servlets/fileServlet", FileServlet.class);
        mappings.add (servletMapping);

        MirandaStatus.initialize();
        MirandaStatus.getInstance().start();

        ClusterStatus.initialize();
        ClusterStatus.getInstance().start();

        long timeoutPeriod = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_SERVLET_TIMEOUT, MirandaProperties.DEFAULT_SERVLET_TIMEOUT);

        LoginHolder.initialize(timeoutPeriod);
        LoginHolder.getInstance().start();

        UserHolder.initialize(timeoutPeriod);
        UserHolder.getInstance().start();

        TopicHolder.initialize(timeoutPeriod);
        TopicHolder.getInstance().start();

        SetupServletsMessage setupServletsMessage = new SetupServletsMessage(getMiranda().getQueue(), this,
                convertToArray(mappings));

        send (getMiranda().getHttp(), setupServletsMessage);
    }


    /**
     * Services are the static variable of {@link Miranda} like {@link Miranda#timer},
     * except for {@link Miranda#properties}.
     *
     * <p>
     *     Note that {@link Miranda#properties} must have already been initialized when
     *     this method is called.
     * </p>
     *
     * <p>
     *     The services that this method initializes:
     * </p>
     * <ul>
     *     <li>{@link Miranda#fileWatcher}</li>
     *     <li>{@link Miranda#timer}</li>
     *     <li>{@link Miranda#commandLine}</li>
     * </ul>
     */
    public void startServices () {
        MirandaProperties properties = Miranda.properties;

        Miranda.fileWatcher = new FileWatcherService(properties.getIntProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD));
        Miranda.fileWatcher.start();

        Miranda.timer = new MirandaTimer();
        Miranda.timer.start();

        Miranda.commandLine = getCommandLine();
    }

    private static class LocalRunnable implements Runnable {
        private String filename;

        public LocalRunnable (String filename) {
            this.filename = filename;
        }

        public void run () {
            DOMConfigurator.configure(filename);
        }
    }


    private void startLogger() {
        DOMConfigurator.configure(getLogConfigurationFile());
    }

    public void processCommandLine() {
        this.commandLine = getCommandLine();
    }


    /**
     * Load the system properties; using the defaults if nothing else is available.
     * <p>
     * The method will use the first argument passed to it, if supplied,
     * otherwise it will use {@link MirandaProperties#PROPERTY_PROPERTIES_FILE if that is defined.
     * If neither is defined then it
     * will look for {@link MirandaProperties#DEFAULT_PROPERTIES_FILENAME} in the current directory.
     * <p>
     * <p>
     * The method always "augments" the system properties with the {@link #DEFAULT_PROPERTIES}
     * if it finds a proerties file it will augment the result with that
     * file.
     */
    private void loadProperties() {
        Miranda.properties = new MirandaProperties(getPropertiesFilename(), Writer.getInstance());
        Miranda.properties.log();
        Miranda.properties.updateSystemProperties();
    }

    /**
     * Start the Miranda subsystems.
     * <p>
     * <p>
     * This method starts the {@link Writer}, the {@link Cluster} and the
     * {@link SocketNetwork} object.  The Cluster may start additional subsystems
     * for the Nodes in the Cluster.
     * </P>
     */
    private void startSubsystems() throws MirandaException {
        MirandaProperties properties = Miranda.properties;
        MirandaFactory factory = new MirandaFactory(properties);
        Miranda miranda = Miranda.getInstance();

        Network network = factory.buildNetwork();
        network.start();

        String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);

        Cluster.initializeClass(filename, Writer.getInstance(), network);
        miranda.setCluster(Cluster.getInstance());

        Cluster.getInstance().sendConnect (null, this);

        SessionManager sessionManager = new SessionManager();
        sessionManager.start();
        miranda.setSessionManager(sessionManager);
    }

    public void startWriter () {
        Writer writer = new Writer();
        writer.start();

        getMiranda().setWriter(writer);
    }

    private void setupHttpServer() {
        try {
            MirandaFactory factory = getMiranda().factory;
            HttpServer httpServer = factory.buildHttpServer();
            Miranda.getInstance().setHttp(httpServer.getQueue());
            setHttpServer(httpServer);
        } catch (MirandaException e) {
            Panic panic = new StartupPanic("Exception trying to create http server", e, StartupPanic.StartupReasons.ExceptionCreatingHttpServer);
            Miranda.getInstance().panic(panic);
        }
    }

    private void loadFiles() {
        try {
            Miranda miranda = Miranda.getInstance();
            MirandaProperties properties = Miranda.properties;

            String clusterFile = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);
            ClusterFile.initialize(clusterFile, getWriter(), Cluster.getInstance().getQueue());
            miranda.setCluster(Cluster.getInstance());

            String filename = properties.getProperty(MirandaProperties.PROPERTY_USERS_FILE);
            UserManager userManager = new UserManager(filename);
            userManager.start();
            miranda.setUserManager(userManager);

            filename = properties.getProperty(MirandaProperties.PROPERTY_TOPICS_FILE);
            TopicManager topicManager = new TopicManager(filename);
            topicManager.start();
            miranda.setTopicManager(topicManager);

            filename = properties.getProperty(MirandaProperties.PROPERTY_SUBSCRIPTIONS_FILE);
            SubscriptionManager subscriptionManager = new SubscriptionManager(filename);
            subscriptionManager.start();
            miranda.setSubscriptionManager(subscriptionManager);

            String directoryName = properties.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
            File f = new File(directoryName);
            directoryName = f.getCanonicalPath();
            SystemMessages messages = new SystemMessages(directoryName, getWriter());
            messages.start();
            messages.load();
            messages.updateVersion();
            miranda.setEvents(messages);

            directoryName = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
            f = new File(directoryName);
            directoryName = f.getCanonicalPath();
            SystemDeliveriesFile deliveriesFile = new SystemDeliveriesFile(directoryName, getWriter());
            deliveriesFile.start();
            deliveriesFile.load();
            deliveriesFile.updateVersion();
            miranda.setDeliveries(deliveriesFile);
        } catch (Exception e) {
            Panic panic = new StartupPanic("Unchecked exception during startup", e, StartupPanic.StartupReasons.UncheckedException);
            Miranda.getInstance().panic(panic);
        }
    }


    public void setupSchedule () {
        MirandaProperties properties = Miranda.properties;

        long garbageCollectionPeriod = properties.getLongProperty(MirandaProperties.PROPERTY_GARBAGE_COLLECTION_PERIOD, MirandaProperties.DEFAULT_GARBAGE_COLLECTION_PERIOD);

        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(Miranda.getInstance().getQueue(),
                Miranda.timer);
        Miranda.timer.sendSchedulePeriodic(garbageCollectionPeriod, getMiranda().getQueue(), garbageCollectionMessage);
    }

    /**
     * Get the log4j configuration file, before the properties have been loaded.
     *
     * <p>
     *     Normally, this would be obtained from the properties file, but since
     *     logging needs to be setup early, this method is provided.
     * </p>
     *
     * @return
     */
    public String getLogConfigurationFile () {
        //
        // if its defined on the command line, use that
        //
        String filename = getCommandLine().getLog4jFilename();

        //
        // otherwise, try for the environment
        //
        if (null == filename) {
            filename = System.getenv().get(MirandaProperties.PROPERTY_LOG4J_FILE);
        }

        //
        // if we still don't have a name, then use a default
        //
        if (null == filename) {
            filename = MirandaProperties.DEFAULT_LOG4J_FILE;
        }

        return filename;
    }

    public void startHttpServer () {
        getHttpServer().sendStart(getMiranda().getQueue());
    }

    public void definePanicPolicy () {
        PanicPolicy panicPolicy = Miranda.factory.buildPanicPolicy();
        Miranda.getInstance().setPanicPolicy(panicPolicy);
    }

    public void defineFactory () {
        Miranda.factory = new MirandaFactory(Miranda.properties);

        setFactory(Miranda.factory);
    }

    public String getPropertiesFilename () {
        String filename = null;

        //
        // Start with the default
        //
        filename = MirandaProperties.DEFAULT_PROPERTIES_FILENAME;

        //
        // if there is a name defined in the environment, use that
        //
        if (System.getenv().get(MirandaProperties.PROPERTY_PROPERTIES_FILE) != null)
        {
            filename = (String) System.getenv().get(MirandaProperties.PROPERTY_PROPERTIES_FILE);
        }

        //
        // if it was on the commad line, use that
        //
        if (getCommandLine().getPropertiesFilename() != null)
        {
            filename = getCommandLine().getPropertiesFilename();
        }

        return filename;
    }

    public void startListening () {
        NetworkListener networkListener = getFactory().buildNetworkListener();
        networkListener.start();

        getMiranda().setNetworkListener(networkListener);
    }
}
