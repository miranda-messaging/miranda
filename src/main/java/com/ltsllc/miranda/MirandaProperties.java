package com.ltsllc.miranda;

/**
 * Created by Clark on 1/5/2017.
 */
public class MirandaProperties {
    public static final String PROPERTY_SYSTEM_PROPERTIES = "com.ltsllc.miranda.Properties";
    public static final String PROPERTY_CLUSTER_FILE = "com.ltsllc.miranda.ClusterFile";
    public static final String PROPERTY_USERS_FILE = "com.ltsllc.miranda.UsersFile";
    public static final String PROPERTY_TOPICS_FILE = "com.ltsllc.miranda.TopicsFile";
    public static final String PROPERTY_SUBSCRIPTIONS_FILE = "com.ltsllc.miranda.SubscriptionsFile";
    public static final String PROPERTY_MESSAGE_PORT = "com.ltsllc.miranda.MessagePort";
    public static final String PROPERTY_CLUSTER_PORT = "com.ltsllc.miranda.ClusterPort";
    public static final String PROPERTY_MESSAGES_DIRECTORY = "com.ltsllc.miranda.MessageDirectory";
    public static final String PROPERTY_DELIVERY_DIRECTORY = "com.ltsllc.miranda.DeliveryDirectory";
    public static final String PROPERTY_LOG4J_FILE = "com.ltsllc.miranda.Log4jFile";

    public static final String DEFAULT_PROPERTIES_FILENAME = "miranda.properties";
    public static final String DEFAULT_CLUSTER_FILE = "data/cluster.json";
    public static final String DEFAULT_USERS_FILE = "data/users.json";
    public static final String DEFAULT_TOPICS_FILE = "data/topics.json";
    public static final String DEFAULT_SUBSCRIPTIONS_FILE = "data/subscriptions.json";
    public static final String DEFAULT_MESSAGE_FILE = "data/message.json";
    public static final String DEFAULT_MESSAGE_PORT = "443";
    public static final String DEFAULT_CLUSTER_PORT = "6789";
    public static final String DEFAULT_DELIVERY_DIRECTORY = "data/deliveries";
    public static final String DEFAULT_MESSAGES_DIRECTORY = "data/messages";
    public static final String DEFAULT_LOG4J_FILE = "log4j.xml";

    public static String[][] DEFAULT_PROPERTIES = {
            {PROPERTY_CLUSTER_FILE, DEFAULT_CLUSTER_FILE},
            {PROPERTY_USERS_FILE, DEFAULT_USERS_FILE},
            {PROPERTY_TOPICS_FILE, DEFAULT_TOPICS_FILE},
            {PROPERTY_SUBSCRIPTIONS_FILE, DEFAULT_SUBSCRIPTIONS_FILE},
            {PROPERTY_MESSAGE_PORT, DEFAULT_MESSAGE_PORT},
            {PROPERTY_CLUSTER_PORT, DEFAULT_CLUSTER_PORT},
            {PROPERTY_MESSAGES_DIRECTORY, DEFAULT_MESSAGES_DIRECTORY},
            {PROPERTY_DELIVERY_DIRECTORY, DEFAULT_DELIVERY_DIRECTORY},
            {PROPERTY_LOG4J_FILE, DEFAULT_LOG4J_FILE}
    };



}
