package com.ltsllc.miranda.property;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

/**
 * The Miranda system's properties.
 *
 * <P>
 *     This class is a SingleFile object that knows a lot about {@link Properties}
 *     objects.
 * </P>
 */
public class MirandaProperties extends SingleFile<String> {
    private static Logger logger = Logger.getLogger(MirandaProperties.class);

    public enum EncryptionModes {
        Unknown,
        None,
        LocalCA,
        RemoteCA
    }

    public enum LoggingLevel {
        Debug,
        Info,
        Warning,
        Error,
        Fatal
    }

    public enum MirandaModes {
        Normal,
        Debugging
    }

    public enum Networks {
        Unknown,
        Netty,
        Socket
    }

    public static final String PACKAGE_NAME = "com.ltsllc.miranda.";

    public static final String HTTP_PACKAGE_NAME = PACKAGE_NAME + "http.";

    public static final String ENCRYPTION_PACKAGE = PACKAGE_NAME + "encryption.";

    public static final String MY_PACKAGE = PACKAGE_NAME + "my.";

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
    public static final String PROPERTY_LOGGING_LEVEL = PACKAGE_NAME + "LoggingLevel";
    public static final String PROPERTY_MESSAGE_FILE_SIZE = "com.ltsllc.miranda.MessageFileSize";
    public static final String PROPERTY_DELAY_BETWEEN_RETRIES = "com.ltsllc.miranda.DelayBetweenRetries";
    public static final String PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD = PACKAGE_NAME + "cluster.HealthCheckPeriod";
    public static final String PROPERTY_CLUSTER_TIMEOUT = PACKAGE_NAME + "cluster.Timeout";
    public static final String PROPERTY_GARBAGE_COLLECTION_PERIOD = PACKAGE_NAME + "GarbageCollectionPeriod";
    public static final String PROPERTY_PROPERTIES_FILE = PACKAGE_NAME + "PropertiesFile";
    public static final String PROPERTY_MIRANDA_MODE = PACKAGE_NAME + "MirandaMode";
    public static final String PROPERTY_NETWORK = PACKAGE_NAME + "Network";

    public static final String PROPERTY_FILE_CHECK_PERIOD = PACKAGE_NAME + "FileCheckPeriod";

    public static final String PROPERTY_PORT = HTTP_PACKAGE_NAME + "Port";

    public static final String PROPERTY_ENCRYPTION_MODE = ENCRYPTION_PACKAGE + "Mode";

    public static final String PROPERTY_TRUST_STORE = ENCRYPTION_PACKAGE + "Truststore";
    public static final String PROPERTY_TRUST_STORE_PASSWORD = ENCRYPTION_PACKAGE + "TruststorePassword";
    public static final String PROPERTY_TRUST_STORE_ALIAS = ENCRYPTION_PACKAGE + "TruststoreAlias";

    public static final String PROPERTY_KEYSTORE = ENCRYPTION_PACKAGE + "KeyStore";
    public static final String PROPERTY_KEYSTORE_PASSWORD = ENCRYPTION_PACKAGE + "KeyStorePassword";
    public static final String PROPERTY_KEYSTORE_ALIAS = ENCRYPTION_PACKAGE + "KeyStoreAlias";

    public static final String PROPERTY_MY_DNS = MY_PACKAGE + "Dns";
    public static final String PROPERTY_MY_IP = MY_PACKAGE + "Ip";
    public static final String PROPERTY_MY_PORT = MY_PACKAGE + "Port";
    public static final String PROPERTY_MY_DESCIPTION = MY_PACKAGE + "Description";

    public static final String DEFAULT_FILE_CHECK_PERIOD  = "1000";
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
    public static final String DEFAULT_LOGGING_LEVEL = LoggingLevel.Warning.toString();
    public static final String DEFAULT_MESSAGE_FILE_SIZE = "100";
    public static final String DEFAULT_DELAY_BETWEEN_RETRIES = "10000";
    public static final String DEFAULT_CLUSTER_HEALTH_CHECK_PERIOD = "86400000"; // once/day
    public static final String DEFAULT_CLUSTER_TIMEOUT = "604800000"; // once week
    public static final String DEFAULT_GARBAGE_COLLECTION_PERIOD = "3600000"; // once/hour
    public static final String DEFAULT_MIRANDA_MODE = MirandaModes.Normal.toString();
    public static final String DEFAULT_NETWORK = Networks.Socket.toString();

    public static final String DEFAULT_ENCRYPION_MODE = "localCA";
    public static final String DEFAULT_TRUST_STORE = "truststore";
    public static final String DEFAULT_TRUST_STORE_ALIAS = "ca";
    public static final String DEFAULT_KEYSTORE = "serverkeystore";
    public static final String DEFAULT_KEYSTORE_ALIAS = "server";


    public static final String DEFAULT_PORT = "443";


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
            {PROPERTY_LOGGING_LEVEL, DEFAULT_LOGGING_LEVEL},
            {PROPERTY_MESSAGE_FILE_SIZE, DEFAULT_MESSAGE_FILE_SIZE},
            {PROPERTY_FILE_CHECK_PERIOD, DEFAULT_FILE_CHECK_PERIOD},
            {PROPERTY_PROPERTIES_FILE, DEFAULT_PROPERTIES_FILENAME},
            {PROPERTY_MIRANDA_MODE, DEFAULT_MIRANDA_MODE},
            {PROPERTY_NETWORK, DEFAULT_NETWORK},

            {PROPERTY_ENCRYPTION_MODE, DEFAULT_ENCRYPION_MODE},
            {PROPERTY_TRUST_STORE, DEFAULT_TRUST_STORE},
            {PROPERTY_TRUST_STORE_ALIAS, DEFAULT_TRUST_STORE_ALIAS},
            {PROPERTY_KEYSTORE, DEFAULT_KEYSTORE},
            {PROPERTY_KEYSTORE_ALIAS, DEFAULT_KEYSTORE_ALIAS},

            {PROPERTY_DELAY_BETWEEN_RETRIES, DEFAULT_DELAY_BETWEEN_RETRIES},
            {PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD, DEFAULT_CLUSTER_HEALTH_CHECK_PERIOD},
            {PROPERTY_CLUSTER_TIMEOUT, DEFAULT_CLUSTER_TIMEOUT},
            {PROPERTY_GARBAGE_COLLECTION_PERIOD, DEFAULT_GARBAGE_COLLECTION_PERIOD},

            {PROPERTY_PORT, DEFAULT_PORT},
    };

    private Properties properties;

    public MirandaProperties (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);

        properties = new Properties();
        load();
    }

    /**
     * Use this constructor for testing only because it <b>it will not work!</b>.
     * In particular calls to things like {@link #load()} and {@link #watch()}
     * will result in a NullPointerException.
     */
    public MirandaProperties()
    {
        super (null, null);
        setupDefaults();
    }

    /**
     * Set the properties to their default values.
     *
     * <P>
     *     This method is intended only for use in testing.
     * </P>
     */
    public void setupDefaults () {
        Properties systemProperties = PropertiesUtils.copy(System.getProperties());
        Properties defaultProperties = PropertiesUtils.buildFrom(DEFAULT_PROPERTIES);

        Properties properties = PropertiesUtils.overwrite(systemProperties, defaultProperties);

        this.properties = PropertiesUtils.copy(properties);
    }

    public void load () {
        //
        // start with the defaults
        //
        Properties defaults = PropertiesUtils.buildFrom(MirandaProperties.DEFAULT_PROPERTIES);

        //
        // overwrite with what's in the system properties
        //
        Properties system = System.getProperties();
        Properties properties = PropertiesUtils.overwrite(defaults, system);

        //
        // overwrite with what's in the properties file
        //
        Properties temp = PropertiesUtils.load(getFilename());
        PropertiesUtils.overwrite(properties, temp);

        //
        // overwrite with whatever was on the command line
        //
        temp = Miranda.commandLine.asProperties();
        PropertiesUtils.overwrite(properties, temp);

        this.properties = properties;
    }


    public void updateSystemProperties()
    {
        PropertiesUtils.overwrite(System.getProperties(), properties);
    }


    public int getIntegerProperty (String name) {
        String temp = getProperty(name);

        if (null == temp)
            return 0;
        else {
            return Integer.parseInt(temp);
        }
    }

    public int getIntProperty (String name) {
        return getIntegerProperty(name);
    }

    public long getLongProperty (String name) {
        String value = getProperty(name);

        if (null == value)
            return 0;
        else
            return Long.parseLong(value);
    }

    public EncryptionModes getEncrptionModeProperty (String name) {
        String value = getProperty(name);

        EncryptionModes mode = EncryptionModes.Unknown;

        if (value.equalsIgnoreCase("none") || value.equalsIgnoreCase("off") || value.equalsIgnoreCase("nossl")) {
            mode = EncryptionModes.None;
        } else if (value.equalsIgnoreCase("local") || value.equalsIgnoreCase("localCA") || value.equalsIgnoreCase("default")) {
            mode = EncryptionModes.LocalCA;
        } else if (value.equalsIgnoreCase("remote") || value.equalsIgnoreCase("remoteCA")) {
            mode = EncryptionModes.RemoteCA;
        }

        return mode;
    }

    public Networks getNetworkProperty (String name) {
        String value = getProperty(name);
        Networks network = Networks.Unknown;

        if (null != value) {
            network = Networks.valueOf(value);
        }

        return network;
    }

    public Networks getNetworkProperty () {
        return getNetworkProperty(PROPERTY_NETWORK);
    }


    public String getProperty (String name) {
        return properties.getProperty(name);
    }

    public void setProperty (String name, String value) {
        properties.setProperty(name, value);
    }

    public Type listType () {
        return new TypeToken<List<String>>() {} .getType();
    }

    public List buildEmptyList () {
        return new ArrayList<String>();
    }

    public void log () {
        PropertiesUtils.log(properties);
    }
}
