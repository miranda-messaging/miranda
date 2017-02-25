package com.ltsllc.miranda.file;

import com.ltsllc.miranda.util.IOUtils;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Clark on 1/21/2017.
 */
public class MirandaProperties extends Properties {
    private static Logger logger = Logger.getLogger(MirandaProperties.class);

    public enum EncryptionModes {
        Unknown,
        None,
        LocalCA,
        RemoteCA
    }

    public static final String PACKAGE_NAME = "com.ltsllc.miranda.";

    public static final String HTTP_PACKAGE_NAME = PACKAGE_NAME + "http.";

    public static final String ENCRYPTION_PACKAGE = PACKAGE_NAME + "encryption.";

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
    public static final String PROPERTY_DELAY_BETWEEN_RETRIES = "com.ltsllc.miranda.DelayBetweenRetries";
    public static final String PROPERTY_MY_DNS = PACKAGE_NAME + "my.Dns";
    public static final String PROPERTY_MY_IP = PACKAGE_NAME + "my.Ip";
    public static final String PROPERTY_MY_PORT = PACKAGE_NAME + "my.Port";
    public static final String PROPERTY_MY_DESCIPTION = PACKAGE_NAME + "my.Description";
    public static final String PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD = PACKAGE_NAME + "cluster.HealthCheckPeriod";
    public static final String PROPERTY_CLUSTER_TIMEOUT = PACKAGE_NAME + "cluster.Timeout";
    public static final String PROPERTY_GARBAGE_COLLECTION_PERIOD = PACKAGE_NAME + "GarbageCollectionPeriod";

    public static final String PROPERTY_PORT = HTTP_PACKAGE_NAME + "Port";

    public static final String PROPERTY_ENCRYPTION_MODE = ENCRYPTION_PACKAGE + "Mode";

    public static final String PROPERTY_TRUST_STORE = ENCRYPTION_PACKAGE + "Truststore";
    public static final String PROPERTY_TRUST_STORE_PASSWORD = ENCRYPTION_PACKAGE + "TruststorePassword";
    public static final String PROPERTY_TRUST_STORE_ALIAS = ENCRYPTION_PACKAGE + "TruststoreAlias";

    public static final String PROPERTY_KEYSTORE = ENCRYPTION_PACKAGE + "KeyStore";
    public static final String PROPERTY_KEYSTORE_PASSWORD = ENCRYPTION_PACKAGE + "KeyStorePassword";
    public static final String PROPERTY_KEYSTORE_ALIAS = ENCRYPTION_PACKAGE + "KeyStoreAlias";


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
    public static final String DEFAULT_DELAY_BETWEEN_RETRIES = "10000";
    public static final String DEFAULT_KEY_STORE_ALIAS = "server";
    public static final String DEFAULT_CLUSTER_HEALTH_CHECK_PERIOD = "86400000"; // once/day
    public static final String DEFAULT_CLUSTER_TIMEOUT = "604800000"; // once week
    public static final String DEFAULT_GARBAGE_COLLECTION_PERIOD = "3600000"; // once/hour

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
            {PROPERTY_MESSAGE_FILE_SIZE, DEFAULT_MESSAGE_FILE_SIZE},

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

    private static MirandaProperties ourInstance;

    public static synchronized void initialize (String filename) {
        if (null == ourInstance) {
            Properties defaults = PropertiesUtils.buildFrom(MirandaProperties.DEFAULT_PROPERTIES);
            Properties sysProps = PropertiesUtils.merge(System.getProperties(), defaults);

            Properties properties = null;

            try {
                properties = PropertiesUtils.load(filename);
            } catch (IOException e) {
                logger.fatal ("Exception trying to load properties from " + filename, e);
            }

            PropertiesUtils.augment(sysProps, properties);
            ourInstance = new MirandaProperties(sysProps);
            ourInstance.updateSystemProperties();
        }
    }

    public static MirandaProperties getInstance() {
        return ourInstance;
    }

    private MirandaProperties (Properties p) {
        Set<String> names = p.stringPropertyNames();
        for (String name : names)
        {
            String value = p.getProperty(name);
            setProperty(name, value);
        }
    }

    public static synchronized void reset() {
        ourInstance = null;
    }

    public void updateSystemProperties()
    {
        PropertiesUtils.setIfNull(System.getProperties(), this);
    }


    public int getIntegerProperty (String name) {
        String temp = getProperty(name);

        if (null == temp)
            return 0;
        else {
            return Integer.parseInt(temp);
        }

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
}
