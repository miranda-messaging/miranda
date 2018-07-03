/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.miranda;

import com.ltsllc.clcl.*;
import com.ltsllc.commons.commadline.CommandLineException;
import com.ltsllc.commons.util.PropertiesUtils;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.deliveries.DeliveryManager;
import com.ltsllc.miranda.event.EventManager;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.http.ServletMapping;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.states.ReadyState;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.UnencryptedConnectionListener;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.panics.StartupPanic;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.servlet.basicstatus.BasicStatusServlet;
import com.ltsllc.miranda.servlet.bootstrap.BootstrapServlet;
import com.ltsllc.miranda.servlet.cluster.ClusterStatus;
import com.ltsllc.miranda.servlet.cluster.ClusterStatusServlet;
import com.ltsllc.miranda.servlet.encrypt.CreateKeyPairServlet;
import com.ltsllc.miranda.servlet.event.EventHolder;
import com.ltsllc.miranda.servlet.event.PublishServlet;
import com.ltsllc.miranda.servlet.file.FileServlet;
import com.ltsllc.miranda.servlet.login.LoginHolder;
import com.ltsllc.miranda.servlet.login.LoginServlet;
import com.ltsllc.miranda.servlet.miranda.MirandaStatus;
import com.ltsllc.miranda.servlet.misc.ShutdownHolder;
import com.ltsllc.miranda.servlet.misc.ShutdownServlet;
import com.ltsllc.miranda.servlet.property.ListPropertiesServlet;
import com.ltsllc.miranda.servlet.property.SetPropertyServlet;
import com.ltsllc.miranda.servlet.status.StatusServlet;
import com.ltsllc.miranda.servlet.subscription.*;
import com.ltsllc.miranda.servlet.topic.*;
import com.ltsllc.miranda.servlet.user.*;
import com.ltsllc.miranda.servlet.user.servlet.*;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.shutdown.ShutdownException;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
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

    private static Logger logger = Logger.getLogger(Startup.class);

    private String[] arguments = {};
    private int index;
    private LogLevel logLevel = LogLevel.NORMAL;
    private HttpServer httpServer;
    private MirandaFactory factory;
    private MirandaProperties properties;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private Reader reader;
    private String keystorePasswordString;
    private String trustorePasswordString;
    private Properties overrideProperties;
    private JavaKeyStore keyStore;
    private JavaKeyStore trustStore;
    private boolean debugMode;

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    public JavaKeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(JavaKeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public JavaKeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(JavaKeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public Properties getOverrideProperties() {
        return overrideProperties;
    }

    public void setOverrideProperties(Properties overrideProperties) {
        this.overrideProperties = overrideProperties;
    }

    public String getTrustorePasswordString() {
        return trustorePasswordString;
    }

    public void setTrustorePasswordString(String trustorePasswordString) {
        this.trustorePasswordString = trustorePasswordString;
    }

    public String getKeystorePasswordString() {
        return keystorePasswordString;
    }

    public void setKeystorePasswordString(String keystorePasswordString) {
        this.keystorePasswordString = keystorePasswordString;
    }

    public String[] getArguments() {
        return arguments;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public MirandaProperties getProperties() {
        return properties;
    }

    public void setProperties(MirandaProperties properties) {
        this.properties = properties;
    }

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
        return Miranda.getInstance().getCommandLine();
    }

    public Miranda getMiranda() {
        return (Miranda) getContainer();
    }

    public Startup(Miranda miranda, String[] argv) throws MirandaException {
        super(miranda);

        this.arguments = argv;
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
            setupPanicPolicy();
            setupLogging();
            processCommandLine();
            processPasswords();
            defineFactory();
            setupProperties();
            definePanicPolicy();
            performMiscellaneousOperations();
            setupKeyStores();
            setupHttpServer();
            startHttpServer();
            setupServlets();
            getKeys(getKeystorePasswordString());
            startLogger();
            logProperties();
            startWriter();
            startReader();
            startServices();
            startSubsystems();
            loadFiles();
            setupSchedule();
            startListening();
            Miranda.getInstance().performGarbageCollection();
            exportCertificate();
            return new ReadyState(getMiranda());
        } catch (Exception e) {
            StartupPanic startupPanic = new StartupPanic("Exception during startup", e, StartupPanic.StartupReasons.StartupFailed);
            Miranda.panicMiranda(startupPanic);
        }

        return StopState.getInstance();
    }

    public void setupLogging() {
        DOMConfigurator.configure("log4j.xml");
    }

    public void processCommandLine() throws CommandLineException {
        MirandaCommandLine mirandaCommandLine = new MirandaCommandLine();
        mirandaCommandLine.parse(getArguments());
        Miranda.getInstance().setCommandLine(mirandaCommandLine);
        setDebugMode(mirandaCommandLine.isDebugMode());
    }

    /**
     * Does the system need to be setup?
     *
     * <p>
     *     The system needs setup when
     *     <ul>
     *         <li>No keystore exists</li>
     *         <li>No truststore exists</li>
     *     </ul>
     * </p>
     * @return
     */
    public boolean systemNeedsSetup() {
        JavaKeyStore keyStore = Miranda.getInstance().getKeyStore();
        JavaKeyStore trustStore = Miranda.getInstance().getTrustStore();

        return keyStore == null || trustStore == null || !keyStore.exists() || !trustStore.exists();
    }

    private void performMiscellaneousOperations() throws MirandaException {
        StopState.initializeClass();
    }


    public void checkProperty(String name) {
        String value = getProperties().getProperty(name);

        if (null != value)
            value = value.trim();

        if (null == value || value.equals("")) {
            String message = "The property, " + name + ", is undefined or empty";
            StartupPanic startupPanic = new StartupPanic(message, StartupPanic.StartupReasons.MissingProperty);
            Miranda.getInstance().panic(startupPanic);
        }
    }

    public void checkProperties(String[] array) {
        for (String s : array) {
            checkProperty(s);
        }
    }

    public void checkProperties(String p1, String p2) {
        String[] properties = {p1, p2};
        checkProperties(properties);
    }

    public void getKeys(String password) throws ShutdownException {
        if (isDebugMode())
            return;
        else if (password == null)
            password = "";

        try {
            String keyStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);

            JavaKeyStore javaKeyStore = new JavaKeyStore(keyStoreFilename, password);

            String alias = getProperties().getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);
            KeyPair keyPair = javaKeyStore.getKeyPair(alias);
            setPublicKey(keyPair.getPublicKey());
            setPrivateKey(keyPair.getPrivateKey());
        } catch (Throwable t) {
            throw new ShutdownException("Exception trying to get keys", t);
        }
    }

    public ServletMapping[] convertToArray(List<ServletMapping> mappings) {
        ServletMapping[] mappingArray = new ServletMapping[mappings.size()];
        for (int i = 0; i < mappingArray.length; i++) {
            mappingArray[i] = mappings.get(i);
        }

        return mappingArray;
    }

    public void setupServlets() throws MirandaException {
        List<ServletMapping> mappings = new ArrayList<ServletMapping>();

        ServletMapping servletMapping = new ServletMapping("/servlets/status", StatusServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/status", StatusServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/basicStatus", BasicStatusServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/properties", ListPropertiesServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/properties/list", ListPropertiesServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/setProperty", SetPropertyServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/properties/", SetPropertyServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/clusterStatus", ClusterStatusServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/login", LoginServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/login", LoginServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/createUser", CreateUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/users/create", CreateUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getUsers", ListUsersServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/users/list", ListUsersServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getUser", ReadUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/users/read", ReadUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/updateUser", UpdateUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/users/update", UpdateUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/deleteUser", DeleteUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/users/delete", DeleteUserServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/createKeyPair", CreateKeyPairServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/key/createKeyPair", CreateKeyPairServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getTopics", ListTopicsServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/topics/list", ListTopicsServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getTopic", ReadTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/topics/read", ReadTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/createTopic", CreateTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/topics/create", CreateTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/updateTopic", UpdateTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/topics/update", UpdateTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/deleteTopic", DeleteTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/topics/delete", DeleteTopicServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/fileServlet", FileServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/createSubscription", CreateSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/subscriptions/create", CreateSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getSubscriptions", ListSubscriptionsServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/subscriptions/list", ListSubscriptionsServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/getSubscription", ReadSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/subscriptions/read", ReadSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/updateSubscription", UpdateSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/subscriptions/update", UpdateSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/deleteSubscription", DeleteSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/subscriptions/delete", DeleteSubscriptionServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/servlets/shutdown", ShutdownServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/publish/*", PublishServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/shutdown", ShutdownServlet.class);
        mappings.add(servletMapping);

        servletMapping = new ServletMapping("/bootstrap", BootstrapServlet.class);
        mappings.add(servletMapping);

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

        SubscriptionHolder.initialize(timeoutPeriod);
        SubscriptionHolder.getInstance().start();

        ShutdownHolder.initialize(timeoutPeriod);
        ShutdownHolder.getInstance().start();

        EventHolder.initialize(timeoutPeriod);
        EventHolder.getInstance().start();

        getMiranda().getHttpServer().addServlets(mappings);
    }


    /**
     * Services are the static variable of {@link Miranda} like {@link Miranda#timer},
     * except for {@link Miranda#properties}.
     * <p>
     * <p>
     * Note that {@link Miranda#properties} must have already been initialized when
     * this method is called.
     * </p>
     * <p>
     * <p>
     * The services that this method initializes:
     * </p>
     * <ul>
     * <li>{@link Miranda#fileWatcher}</li>
     * <li>{@link Miranda#timer}</li>
     * <li>{@link Miranda#commandLine}</li>
     * </ul>
     */
    public void startServices() throws MirandaException {
        MirandaProperties properties = Miranda.properties;

        Miranda.fileWatcher = new FileWatcherService(properties.getIntProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD));
        Miranda.fileWatcher.start();

        Miranda.timer = new MirandaTimer();
        Miranda.timer.start();
    }

    private static class LocalRunnable implements Runnable {
        private String filename;

        public LocalRunnable(String filename) {
            this.filename = filename;
        }

        public void run() {
            DOMConfigurator.configure(filename);
        }
    }


    private void startLogger() {
        DOMConfigurator.configure(getLogConfigurationFile());
        logger = Logger.getLogger(Startup.class);
        Miranda.setLogger(logger);
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
    private void setupProperties() {
        Miranda.properties = new MirandaProperties(getPropertiesFilename());

        if (null != getOverrideProperties()) {
            Properties p = Miranda.properties.getProperties();
            PropertiesUtils.overwrite(p, getOverrideProperties());
            Miranda.properties.setProperties(p);
        }

        Miranda.properties.updateSystemProperties();

        this.properties = Miranda.properties;

        String trustStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME);
        File file = new File(trustStoreFilename);
        if (!file.exists()) {
            logger.info("The tuststore does not exist, the system will be unable to join a cluster");
        }

        System.setProperty("javax.net.ssl.trustStore", trustStoreFilename);
        Miranda.factory.setProperties(getProperties());
    }

    public void startSubsystems() throws MirandaException {
        MirandaProperties properties = Miranda.properties;
        MirandaFactory factory = new MirandaFactory(properties, getKeystorePasswordString(), getCommandLine().getTrustorePassword());
        Miranda miranda = Miranda.getInstance();


        try {
            Network network = null;

            if (isDebugMode())
                network = factory.buildNetwork();
            else
                network = factory.buildNetwork(getKeyStore().getJsKeyStore(), getTrustStore().getJsKeyStore());

            network.start();
            miranda.setNetwork(network);
            String filename = properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_FILE);
            Cluster cluster = new Cluster(miranda.getNetwork(), filename);
            miranda.setCluster(cluster);
            cluster.start();
        } catch (Exception e) {
            throw new MirandaException(e);
        }

        SessionManager sessionManager = new SessionManager();
        sessionManager.start();
        miranda.setSessionManager(sessionManager);
    }

    public void startWriter() throws MirandaException {
        Writer writer = new Writer(isDebugMode(), getPublicKey());
        writer.start();

        logger.info(getMiranda());

        getMiranda().setWriter(writer);
    }

    public void startReader() throws MirandaException {
        Reader reader = new Reader(isDebugMode(), getPrivateKey());
        reader.start();

        getMiranda().setReader(reader);
    }

    private void setupHttpServer() {
        try {
            MirandaFactory factory = getMiranda().factory;
            HttpServer httpServer = factory.buildServletContainer();
            getMiranda().setHttpServer(httpServer);
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

            String directoryName = properties.getProperty(MirandaProperties.PROPERTY_EVENT_DIRECTORY);
            File f = new File(directoryName);
            directoryName = f.getCanonicalPath();
            int objectLimit = properties.getIntProperty(MirandaProperties.PROPERTY_EVENT_OBJECT_LIMIT);
            EventManager eventManager = new EventManager(directoryName, objectLimit, getReader(), getWriter());
            eventManager.start();
            miranda.setEventManager(eventManager);

            directoryName = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
            f = new File(directoryName);
            directoryName = f.getCanonicalPath();
            objectLimit = properties.getIntProperty(MirandaProperties.PROPERTY_DELIVERY_OBJECT_LIMIT);
            DeliveryManager deliveryManager = new DeliveryManager(directoryName, objectLimit, getReader(), getWriter());
            miranda.setDeliveryManager(deliveryManager);

        } catch (Exception e) {
            Panic panic = new StartupPanic("Unchecked exception during startup", e, StartupPanic.StartupReasons.UncheckedException);
            Miranda.getInstance().panic(panic);
        }
    }


    public void setupSchedule() {
        MirandaProperties properties = Miranda.properties;

        long garbageCollectionPeriod = properties.getLongProperty(MirandaProperties.PROPERTY_GARBAGE_COLLECTION_PERIOD, MirandaProperties.DEFAULT_GARBAGE_COLLECTION_PERIOD);

        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(Miranda.getInstance().getQueue(),
                Miranda.timer);
        Miranda.timer.sendSchedulePeriodic(0, garbageCollectionPeriod, getMiranda().getQueue(), garbageCollectionMessage);
    }

    /**
     * Get the log4j configuration file, before the properties have been loaded.
     * <p>
     * <p>
     * Normally, this would be obtained from the properties file, but since
     * logging needs to be setup early, this method is provided.
     * </p>
     *
     * @return
     */
    public String getLogConfigurationFile() {
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

    public void startHttpServer() throws MirandaException {
        getMiranda().getHttpServer().sendStart(getMiranda().getQueue());
    }

    public void definePanicPolicy() {
        PanicPolicy panicPolicy = Miranda.factory.buildPanicPolicy();
        Miranda.getInstance().setPanicPolicy(panicPolicy);
    }

    public void defineFactory() {
        Miranda.factory = new MirandaFactory(Miranda.properties, getKeystorePasswordString(), getTrustorePasswordString());

        setFactory(Miranda.factory);
    }

    public String getPropertiesFilename() {
        String filename = null;

        //
        // Start with the default
        //
        filename = MirandaProperties.DEFAULT_PROPERTIES_FILENAME;

        //
        // if there is a name defined in the environment, use that
        //
        if (System.getenv().get(MirandaProperties.PROPERTY_PROPERTIES_FILE) != null) {
            filename = (String) System.getenv().get(MirandaProperties.PROPERTY_PROPERTIES_FILE);
        }

        //
        // if it was on the commad line, use that
        //
        if (getCommandLine().getPropertiesFilename() != null) {
            filename = getCommandLine().getPropertiesFilename();
        }

        return filename;
    }

    public void startListening() throws MirandaException {
        String portString = Miranda.properties.getProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        int port = Integer.parseInt(portString);
        if (isDebugMode()) {
            UnencryptedConnectionListener unencryptedConnectionListener = getFactory().
                    buildUnencryptedConnectionListener(getMiranda().getNetwork(), getMiranda().getCluster(), port);

            getMiranda().setNetworkListener(unencryptedConnectionListener);
            unencryptedConnectionListener.start();
        } else {
            //ConnectionListener networkListener = getFactory().buildNetworkListener(getKeyStore(), getTrustStore(),
            //        Miranda.getInstance().getNetwork());
            //getMiranda().setNetworkListener(networkListener);
            //networkListener.start();
        }
    }


    public void processPasswords() {
        if (isDebugMode())
            ;
        else if (null != getCommandLine().getKeystorePassword()) {
            setKeystorePasswordString(getCommandLine().getKeystorePassword());
        } else {
            System.out.println("Enter keystore password> ");
            Scanner scanner = new Scanner(Miranda.inputStream);
            String temp = scanner.nextLine();
            setKeystorePasswordString(temp);
        }

        if (isDebugMode())
            ;
        else if (null != getCommandLine().getTrustorePassword())
            setTrustorePasswordString(getCommandLine().getTrustorePassword());
        else {
            System.out.print("Enter truststore password> ");
            Scanner scanner = new Scanner(Miranda.inputStream);
            String temp = scanner.nextLine();
            setTrustorePasswordString(temp);
        }
    }

    public KeyStore loadKeyStore(String filename, String password) {
        try {
            return Utils.loadKeyStore(filename, password);
        } catch (Exception e) {
            StartupPanic startupPanic = new StartupPanic("Exception loading keystore from " + filename, e,
                    StartupPanic.StartupReasons.ExceptionLoadingKeystore);
            Miranda.panicMiranda(startupPanic);
        }

        return null;
    }

    public void setupKeyStores() throws Panic {
        try {
            if (isDebugMode())
                ;
            else if (null == getKeyStore()) {
                String filename = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);
                this.keyStore = new JavaKeyStore(filename, getKeystorePasswordString());
                Miranda.getInstance().setKeyStore(keyStore);
            }

            if (isDebugMode())
                ;
            else if (null == getTrustStore()) {
                String filename = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME);
                this.trustStore = new JavaKeyStore(filename, getTrustorePasswordString());
                Miranda.getInstance().setTrustStore(trustStore);
            }
        } catch (EncryptionException e) {
            throw new StartupPanic("Exception trying to load keys", e, StartupPanic.StartupReasons.ExceptionLoadingKeystore);
        }
    }


    public void logProperties() {
        Miranda.properties.log();
    }

    public void exportCertificate() throws Exception {
        if (isDebugMode()) {

        } else {

            KeyStore keyStore = getKeyStore().getJsKeyStore();
            Certificate certificate = keyStore.getCertificate("private");
            if (null != certificate)
                com.ltsllc.clcl.Certificate.writeAsPem("tempfile", certificate);
        }
    }

    public void setupPanicPolicy () {
        int maxPanics = Integer.parseInt(MirandaProperties.DEFAULT_PANIC_LIMIT);
        long panicTimeout = Long.parseLong(MirandaProperties.DEFAULT_PANIC_TIMEOUT);
        MirandaPanicPolicy mirandaPanicPolicy = new MirandaPanicPolicy (maxPanics, panicTimeout, Miranda.getInstance(), Miranda.timer);
        Miranda.getInstance().setPanicPolicy(mirandaPanicPolicy);
    }
}
