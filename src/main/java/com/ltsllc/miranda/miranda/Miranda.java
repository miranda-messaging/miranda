package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.node.NodeElement;
import com.ltsllc.miranda.node.messages.UserAddedMessage;
import com.ltsllc.miranda.node.messages.UserDeletedMessage;
import com.ltsllc.miranda.node.messages.UserUpdatedMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.property.NewPropertiesMessage;
import com.ltsllc.miranda.servlet.messages.GetStatusMessage;
import com.ltsllc.miranda.servlet.objects.Property;
import com.ltsllc.miranda.servlet.objects.StatusObject;
import com.ltsllc.miranda.servlet.objects.UserObject;
import com.ltsllc.miranda.session.*;
import com.ltsllc.miranda.subsciptions.Subscription;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;
import com.ltsllc.miranda.subsciptions.messages.CreateSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.DeleteSubscriptionMessage;
import com.ltsllc.miranda.subsciptions.messages.UpdateSubscriptionMessage;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.TopicManager;
import com.ltsllc.miranda.topics.messages.DeleteTopicMessage;
import com.ltsllc.miranda.user.User;
import com.ltsllc.miranda.user.UserManager;
import com.ltsllc.miranda.user.messages.*;
import com.ltsllc.miranda.writer.Writer;
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
    private UserManager userManager;
    private TopicManager topicManager;
    private SubscriptionManager subscriptionManager;
    private SystemMessages events;
    private SystemDeliveriesFile deliveries;
    private Cluster cluster;
    private PanicPolicy panicPolicy;
    private NetworkListener networkListener;
    private SessionManager sessionManager;
    private Writer writer;

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
        userManager = null;
        topicManager = null;
        subscriptionManager = null;
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

        sendIfNotNull(message, getCluster());
        sendIfNotNull(message, getHttp());

        sendIfNotNull(message, fileWatcher);
        sendIfNotNull(message, properties);
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

    public void sendAddSessionMessage(BlockingQueue<Message> senderQueue, Object sender, Session session) {
        AddSessionMessage addSessionMessage = new AddSessionMessage(senderQueue, sender, session);
        sendToMe(addSessionMessage);
    }

    public void sendSessionsExpiredMessage (BlockingQueue<Message> senderQueue, Object sender, List<Session> expiredSessions) {
        SessionsExpiredMessage sessionsExpiredMessage = new SessionsExpiredMessage(senderQueue, sender, expiredSessions);
        sendToMe(sessionsExpiredMessage);
    }

    public void sendDeleteTopicMessage (BlockingQueue<Message> senderQueue, Object sender, String topicName) {
        DeleteTopicMessage deleteTopicMessage = new DeleteTopicMessage(senderQueue, sender, topicName);
        sendToMe(deleteTopicMessage);
    }

    public void sendDeleteUserMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        DeleteUserMessage deleteUserMessage = new DeleteUserMessage(senderQueue, sender, name);
        sendToMe(deleteUserMessage);
    }

    public void sendCreateUserMessage (BlockingQueue<Message> senderQueue, Object sender, User user) {
        CreateUserMessage createUserMessage = new CreateUserMessage(senderQueue, sender, user);
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

    public void sendCreateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Subscription subscription) {
        CreateSubscriptionMessage createSubscriptionMessage = new CreateSubscriptionMessage(senderQueue, sender, subscription);
        sendToMe(createSubscriptionMessage);
    }

    public void sendUpdateSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, Subscription subscription) {
        UpdateSubscriptionMessage updateSubscriptionMessage = new UpdateSubscriptionMessage(senderQueue, sender, subscription);
        sendToMe(updateSubscriptionMessage);
    }

    public void sendDeleteSubscriptionMessage (BlockingQueue<Message> senderQueue, Object sender, String name) {
        DeleteSubscriptionMessage deleteSubscriptionMessage = new DeleteSubscriptionMessage(senderQueue, sender, name);
        sendToMe(deleteSubscriptionMessage);
    }

    public void sendAuctionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        AuctionMessage auctionMessage = new AuctionMessage(senderQueue, sender);
        sendToMe(auctionMessage);
    }
}
