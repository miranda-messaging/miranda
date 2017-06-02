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

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.deliveries.DeliveryManager;
import com.ltsllc.miranda.event.EventManager;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.miranda.messages.AuctionMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.miranda.states.ShuttingDownState;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.messages.UserAddedMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.messages.UserUpdatedMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.property.NewPropertiesMessage;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.servlet.property.Property;
import com.ltsllc.miranda.servlet.status.GetStatusMessage;
import com.ltsllc.miranda.servlet.status.StatusObject;
import com.ltsllc.miranda.session.AddSessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.session.SessionManager;
import com.ltsllc.miranda.session.SessionsExpiredMessage;
import com.ltsllc.miranda.subsciptions.Subscription;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.topics.messages.DeleteTopicMessage;
import com.ltsllc.miranda.topics.messages.UpdateTopicMessage;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.messages.CreateUserMessage;
import com.ltsllc.miranda.user.messages.DeleteUserMessage;
import com.ltsllc.miranda.user.messages.LoginMessage;
import com.ltsllc.miranda.user.messages.UpdateUserMessage;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Miranda system.
 *
 * This is also the main class for the system.
 *
 */
public class Miranda extends Consumer {
    public static final String NAME = "miranda";

    private static Logger logger = Logger.getLogger(Miranda.class);
    private static Miranda ourInstance;

    public static FileWatcherService fileWatcher;
    public static MirandaTimer timer;
    public static MirandaProperties properties;
    public static MirandaFactory factory;
    public static boolean panicking = false;
    public static InputStream inputStream;

    private HttpServer httpServer;
    private UserManager userManager;
    private TopicManager topicManager;
    private SubscriptionManager subscriptionManager;
    private EventManager eventManager;
    private DeliveryManager deliveryManager;
    private Cluster cluster;
    private PanicPolicy panicPolicy;
    private NetworkListener networkListener;
    private SessionManager sessionManager;
    private Writer writer;
    private Reader reader;
    private List<String> waitingOn;
    private Network network;
    public MirandaCommandLine commandLine;
    private KeyStore keyStore;
    private KeyStore trustStore;

    public KeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(KeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public KeyStore getKeyStore() {

        return keyStore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public MirandaCommandLine getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(MirandaCommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public static InputStream getInputStream() {
        return inputStream;
    }

    public static void setInputStream(InputStream inputStream) {
        Miranda.inputStream = inputStream;
    }

    public List<String> getWaitingOn() {
        return waitingOn;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public NetworkListener getNetworkListener() {
        return networkListener;
    }

    public void setNetworkListener(NetworkListener networkListener) {
        this.networkListener = networkListener;
    }

    public PanicPolicy getPanicPolicy() {
        return panicPolicy;
    }

    public void setPanicPolicy(PanicPolicy panicPolicy) {
        this.panicPolicy = panicPolicy;
    }

    public static Logger getLogger () {
        return logger;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public TopicManager getTopicManager() {
        return topicManager;
    }

    public void setTopicManager(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    public SubscriptionManager getSubscriptionManager () {
        return subscriptionManager;
    }

    public void setSubscriptionManager (SubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public DeliveryManager getDeliveryManager() {
        return deliveryManager;
    }

    public void setDeliveryManager(DeliveryManager deliveryManager) {
        this.deliveryManager = deliveryManager;
    }

    public Miranda (String arguments) {
        String[] argv = arguments.split(" |\t");
        basicConstructor(argv);
    }

    public static void setOurInstance(Miranda ourInstance) {
        Miranda.ourInstance = ourInstance;
    }

    public void basicConstructor (String[] argv) {
        super.basicConstructor(NAME);

        ourInstance = this;

        State s = new Startup(this, argv);
        setCurrentStateWithoutStart(s);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);

        inputStream = System.in;
    }

    public Miranda (String[] argv) {
        basicConstructor(argv);
    }

    public Miranda () {
        super("miranda");
        ourInstance = this;

        String[] emptyArgv = new String[0];
        State s = new Startup(this, emptyArgv);
        setCurrentStateWithoutStart(s);

        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);

        inputStream = System.in;

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
    public void panic (Panic panic) {
        PanicPolicy panicPolicy = getPanicPolicy();
        if (null == panicPolicy) {
            throw new ShutdownException("null panic policy in panic", panic);
        }

        panicPolicy.panic(panic);
    }

    public static void main(String[] argv) {
        logger.info ("Starting");
        Miranda miranda = new Miranda(argv);
        miranda.start();
    }

    public void start (String argString, KeyStore keyStore, KeyStore trustStore) {
        String[] argv = argString.split(" |\t");
        MirandaCommandLine mirandaCommandLine = new MirandaCommandLine(argv);
        setCommandLine(mirandaCommandLine);

        Startup startup = (Startup) getCurrentState();
        if (null != startup) {
            startup.setCommandLine(mirandaCommandLine);
            startup.setKeyStore(getKeyStore());
            startup.setTrustStore(getTrustStore());
        }

        start();
    }

    public void start (Properties p) {
        Startup startup = (Startup) getCurrentState();
        startup.setOverrideProperties(p);

        start();
    }

    public void reset () {
        fileWatcher = null;
        properties = null;
        timer = null;
        logger = null;

        httpServer = null;
        userManager = null;
        topicManager = null;
        subscriptionManager = null;
        eventManager = null;
        deliveryManager = null;
        panicPolicy = null;
    }


    public void performGarbageCollection() {
        GarbageCollectionMessage message = new GarbageCollectionMessage(null, this);
        send(message, getQueue());
    }

    public void stop () {
        HttpServer httpServer = getHttpServer();
        if (null != httpServer)
            httpServer.sendStop(getQueue(), this);


        StopState stop = StopState.getInstance();
        setCurrentState(stop);

        StopMessage message = new StopMessage(getQueue(), this);

        sendIfNotNull(message, getCluster());

        sendIfNotNull(message, fileWatcher);
        sendIfNotNull(message, timer);

        if (null != getCluster()) {
            getCluster().sendStop(getQueue(), this);
        }

        if (null != getUserManager()) {
            getUserManager().sendStop(getQueue(), this);
        }

        if (null != getTopicManager()) {
            getTopicManager().sendStop(getQueue(), this);
        }

        if (null != getSubscriptionManager()) {
            getSubscriptionManager().sendStop(getQueue(), this);
        }

        if (null != getNetworkListener()) {
            getNetworkListener().sendStop(getQueue(), this);
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

        if (null != getUserManager())
            getUserManager().sendShutdown(getQueue(), this);

        if (null != getTopicManager())
            getTopicManager().sendShutdown(getQueue(), this);

        if (null != getSubscriptionManager())
            getSubscriptionManager().sendShutdown(getQueue(), this);

        if (null != getEventManager())
            getEventManager().sendShutdown(getQueue(), this);

        if (null != getDeliveryManager())
            getDeliveryManager().sendShutdown(getQueue(), this);

        if (null != fileWatcher)
            fileWatcher.sendShutdown(getQueue(), this);

        if (null != timer)
            timer.sendShutdown(getQueue(), this);

        if (null != getNetworkListener())
            getNetworkListener().sendShutdown(getQueue(), this);

        setCurrentState(new ShuttingDownState(this));
    }

    public void sendAddSessionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session) {
        AddSessionMessage addSessionMessage = new AddSessionMessage(senderQueue, sender, session);
        sendToMe(addSessionMessage);
    }

    public void sendSessionsExpiredMessage (BlockingQueue<Message> senderQueue, Object sender, List<Session> expiredSessions) {
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(senderQueue, sender, expiredSessions);
        sendToMe(sessionsExpiredMessage);
    }

    public void sendDeleteTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, String topicName) {
        DeleteTopicMessage deleteTopicMessage = new DeleteTopicMessage(senderQueue, sender, session, topicName);
        sendToMe(deleteTopicMessage);
    }

    public void sendDeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, String name) {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(senderQueue, sender, session, name);
        sendToMe(deleteUserMessage);
    }

    public void sendCreateUserMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, User user) {
        CreateUserMessage createUserMessage = new CreateUserMessage(senderQueue, sender, session, user);
        sendToMe(createUserMessage);
    }

    public void sendUpdateUserMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, User user) {
        UpdateUserMessage updateUserMessage = new UpdateUserMessage(senderQueue, sender, session, user);
        sendToMe(updateUserMessage);
    }

    public void sendUserAddedMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        UserAddedMessage userAddedMessage = new UserAddedMessage(senderQueue, sender, user);
        sendToMe(userAddedMessage);
    }

    public void sendUserUpdatedMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        UserUpdatedMessage userUpdatedMessage = new UserUpdatedMessage(senderQueue, sender, user);
        sendToMe(userUpdatedMessage);
    }

    public void sendUserDeletedMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        UserDeletedMessage userDeletedMessage = new UserDeletedMessage(senderQueue, sender, name);
        sendToMe(userDeletedMessage);
    }

    public void sendLoginMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        LoginMessage loginMessage = new LoginMessage(senderQueue, sender, name);
        sendToMe(loginMessage);
    }

    public void sendCreateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               Subscription subscription) {
        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(senderQueue, sender, session,
                subscription);

        sendToMe(createSubscriptionMessage);
    }

    public void sendUpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               Subscription subscription) {
        UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(senderQueue, sender, session,
                subscription);

        sendToMe(updateSubscriptionMessage);
    }

    public void sendDeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Session session,
                                               String name) {
        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(senderQueue, sender, session,
                name);

        sendToMe(deleteSubscriptionMessage);
    }

    public void sendAuctionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        AuctionMessage auctionMessage = new AuctionMessage(senderQueue, sender);
        sendToMe(auctionMessage);
    }

    public void sendUpdateTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, Topic topic) {
        UpdateTopicMessage updateTopicMessage = new UpdateTopicMessage(senderQueue, sender, session, topic);
        sendToMe(updateTopicMessage);
    }

    public void initializeWaitingOn () {
        waitingOn = new ArrayList<String>();
        waitingOn.add(Cluster.NAME);
        waitingOn.add(UserManager.NAME);
        waitingOn.add(TopicManager.NAME);
        waitingOn.add(SubscriptionManager.NAME);
        waitingOn.add(EventManager.NAME);
        waitingOn.add(DeliveryManager.NAME);
        waitingOn.add(NetworkListener.NAME);
    }

    public void subsystemShutDown (String subsystem) {
        String match = null;

        for (String s : getWaitingOn())
            if (s.equals(subsystem))
                match = s;

        if (match == null) {
            logger.warn ("Got shutdown response from unrecognized system, " + subsystem);
        } else {
            logger.info("Got shutdown response from " + subsystem);
            getWaitingOn().remove(match);
        }
    }

    public boolean readyToShutDown () {
        return getWaitingOn().size() < 1;
    }

    public static void panicMiranda (Panic panic) {
        Miranda instance = Miranda.getInstance();
        if (null == instance) {
            throw new ShutdownException("null instance in panicMiranda", panic);
        }

        instance.panic(panic);
    }


    public void run () {
        super.run();
    }
}
