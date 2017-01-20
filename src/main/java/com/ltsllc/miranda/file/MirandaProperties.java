package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class MirandaProperties extends SingleFile {
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
    public static final String PROPERTY_MESSAGE_FILE_SIZE = "com.ltsllc.miranda.MessageFileSize";

    public static final String DEFAULT_PROPERTIES_FILENAME = "miranda.properties";
    public static final String DEFAULT_CLUSTER_FILE = "data/cluster.json";
    public static final String DEFAULT_USERS_FILE = "data/users.json";
    public static final String DEFAULT_TOPICS_FILE = "data/topics.json";
    public static final String DEFAULT_SUBSCRIPTIONS_FILE = "data/subscriptions.json";
    public static final String DEFAULT_MESSAGE_PORT = "443";
    public static final String DEFAULT_CLUSTER_PORT = "6789";
    public static final String DEFAULT_DELIVERY_DIRECTORY = "data/deliveries";
    public static final String DEFAULT_MESSAGES_DIRECTORY = "data/messages";
    public static final String DEFAULT_LOG4J_FILE = "log4j.xml";
    public static final String DEFAULT_MESSAGE_FILE_SIZE = "100";

    public static String[][] DEFAULT_PROPERTIES = {
            {PROPERTY_CLUSTER_FILE, DEFAULT_CLUSTER_FILE},
            {PROPERTY_USERS_FILE, DEFAULT_USERS_FILE},
            {PROPERTY_TOPICS_FILE, DEFAULT_TOPICS_FILE},
            {PROPERTY_SUBSCRIPTIONS_FILE, DEFAULT_SUBSCRIPTIONS_FILE},
            {PROPERTY_MESSAGE_PORT, DEFAULT_MESSAGE_PORT},
            {PROPERTY_CLUSTER_PORT, DEFAULT_CLUSTER_PORT},
            {PROPERTY_MESSAGES_DIRECTORY, DEFAULT_MESSAGES_DIRECTORY},
            {PROPERTY_DELIVERY_DIRECTORY, DEFAULT_DELIVERY_DIRECTORY},
            {PROPERTY_LOG4J_FILE, DEFAULT_LOG4J_FILE},
            {PROPERTY_MESSAGE_FILE_SIZE, DEFAULT_MESSAGE_FILE_SIZE}
    };


    private static Logger logger = Logger.getLogger(MirandaProperties.class);
    private static MirandaProperties ourInstance;

    private Map<String, Object> values;

    private MirandaProperties (String filename, BlockingQueue<Message> writerQueue)
    {
        super(filename, writerQueue);

    }

    public void load ()
    {
        Properties p = System.getProperties();
        Properties defaults = PropertiesUtils.buildFrom(DEFAULT_PROPERTIES);

        PropertiesUtils.augment(p, defaults);

        File file = new File(getFilename());

        if (file.exists())
        {
            Properties temp = new Properties();
            PropertiesUtils.augment(p, temp);
        }
    }

    public static synchronized void initialize (String filename, BlockingQueue<Message> writerQueue) {
        if (null == ourInstance) {
            ourInstance = new MirandaProperties(filename, writerQueue);
            ourInstance.load();
            ourInstance.parse();
            ourInstance.watch();
        }
    }

    public static MirandaProperties getInstane()
    {
        return ourInstance;
    }

    public void parse () {
        Properties p = System.getProperties();
        parseInt(PROPERTY_MESSAGE_FILE_SIZE, DEFAULT_MESSAGE_FILE_SIZE, p);
    }



    public void parseInt (String name, String defaultValue, Properties properties) {
        String s = properties.getProperty(name);
        Integer value = null;

        if (null == s) {
            logger.info ("no value for " + name + ", using default value " + defaultValue);
            s = defaultValue;
        }

        try {
            value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            logger.warn ("Exception parsing " + s + ", using default value (" + defaultValue +")", e);
            value = PropertiesUtils.parseIntOrDie(name, defaultValue);
        }

        logger.info (name + " = " + value);
    }


    private int getInt (String name)
    {
        Object o = values.get (name);
        if (null == o)
        {
            Exception e = new Exception();
            logger.fatal (name + " is null", e);
            System.exit(1);
        }

        Integer i = (Integer) o;
        return i.intValue();
    }


    public int getMessageFileSize ()
    {
        return getInt(PROPERTY_MESSAGE_FILE_SIZE);
    }


}
