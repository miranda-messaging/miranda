package com.ltsllc.miranda.property;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.objects.Property;
import com.ltsllc.miranda.util.PropertiesUtils;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
        Socket,
        Mina
    }

    public enum WebSevers {
        Netty,
        Jetty
    }

    public static final String PACKAGE_NAME = "com.ltsllc.miranda.";

    public static final String HTTP_PACKAGE_NAME = PACKAGE_NAME + "http.";

    public static final String ENCRYPTION_PACKAGE = PACKAGE_NAME + "encryption.";

    public static final String MY_PACKAGE = PACKAGE_NAME + "my.";

    public static final String PANIC_PACKAGE = PACKAGE_NAME + "panic.";

    public static final String CLUSTER_PACKAGE = PACKAGE_NAME + "cluster.";

    public static final String PROPERTY_DELAY_BETWEEN_RETRIES = "com.ltsllc.miranda.DelayBetweenRetries";
    public static final String PROPERTY_DELIVERY_DIRECTORY = "com.ltsllc.miranda.DeliveryDirectory";
    public static final String PROPERTY_FILE_CHECK_PERIOD = PACKAGE_NAME + "FileCheckPeriod";
    public static final String PROPERTY_GARBAGE_COLLECTION_PERIOD = PACKAGE_NAME + "GarbageCollectionPeriod";
    public static final String PROPERTY_LOG4J_FILE = "com.ltsllc.miranda.Log4jFile";
    public static final String PROPERTY_MAX_WRITE_FAILURES = PACKAGE_NAME + "MaxWriteFailures";
    public static final String PROPERTY_MESSAGES_DIRECTORY = "com.ltsllc.miranda.MessageDirectory";
    public static final String PROPERTY_MESSAGE_FILE_SIZE = "com.ltsllc.miranda.MessageFileSize";
    public static final String PROPERTY_MESSAGE_PORT = "com.ltsllc.miranda.MessagePort";
    public static final String PROPERTY_NETWORK = PACKAGE_NAME + "Network";
    public static final String PROPERTY_PROPERTIES_FILE = PACKAGE_NAME + "PropertiesFile";
    public static final String PROPERTY_SUBSCRIPTIONS_FILE = "com.ltsllc.miranda.SubscriptionsFile";
    public static final String PROPERTY_TOPICS_FILE = "com.ltsllc.miranda.TopicsFile";
    public static final String PROPERTY_USERS_FILE = "com.ltsllc.miranda.UsersFile";

    public static final String PROPERTY_CLUSTER_FILE = CLUSTER_PACKAGE + "File";
    public static final String PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD = CLUSTER_PACKAGE + "HealthCheckPeriod";
    public static final String PROPERTY_CLUSTER_TIMEOUT = CLUSTER_PACKAGE + "Timeout";
    public static final String PROPERTY_CLUSTER_PORT = CLUSTER_PACKAGE + "Port";

    public static final String PROPERTY_KEYSTORE = ENCRYPTION_PACKAGE + "KeyStore";
    public static final String PROPERTY_KEYSTORE_PASSWORD = ENCRYPTION_PACKAGE + "KeyStorePassword";
    public static final String PROPERTY_KEYSTORE_ALIAS = ENCRYPTION_PACKAGE + "KeyStoreAlias";
    public static final String PROPERTY_CERTIFICATE_PASSWORD = ENCRYPTION_PACKAGE + "CertificatePassword";
    public static final String PROPERTY_CERTIFICATE_ALIAS = ENCRYPTION_PACKAGE + "CertificateAlias";
    public static final String PROPERTY_ENCRYPTION_MODE = ENCRYPTION_PACKAGE + "Mode";

    public static final String PROPERTY_PANIC_LIMIT = PANIC_PACKAGE + "Limit";
    public static final String PROPERTY_PANIC_TIMEOUT = PANIC_PACKAGE + "Timeout";

    public static final String PROPERTY_HTTP_SSL_PORT = HTTP_PACKAGE_NAME + "SslPort";
    public static final String PROPERTY_HTTP_PORT = HTTP_PACKAGE_NAME + "HttpPort";
    public static final String PROPERTY_HTTP_BASE = HTTP_PACKAGE_NAME + "Base";
    public static final String PROPERTY_HTTP_SERVER = HTTP_PACKAGE_NAME + "Server";

    public static final String PROPERTY_TRUST_STORE = ENCRYPTION_PACKAGE + "Truststore";
    public static final String PROPERTY_TRUST_STORE_PASSWORD = ENCRYPTION_PACKAGE + "TruststorePassword";
    public static final String PROPERTY_TRUST_STORE_ALIAS = ENCRYPTION_PACKAGE + "TruststoreAlias";

    public static final String PROPERTY_MY_DNS = MY_PACKAGE + "Dns";
    public static final String PROPERTY_MY_IP = MY_PACKAGE + "Ip";
    public static final String PROPERTY_MY_PORT = MY_PACKAGE + "Port";
    public static final String PROPERTY_MY_DESCIPTION = MY_PACKAGE + "Description";

    public static final String DEFAULT_FILE_CHECK_PERIOD  = "1000";
    public static final String DEFAULT_PROPERTIES_FILENAME = "miranda.properties";
    public static final String DEFAULT_USERS_FILE = "data/users.json";
    public static final String DEFAULT_TOPICS_FILE = "data/topics.json";
    public static final String DEFAULT_SUBSCRIPTIONS_FILE = "data/subscriptions.json";
    public static final String DEFAULT_MESSAGE_PORT = "443";
    public static final String DEFAULT_DELIVERY_DIRECTORY = "data/deliveries";
    public static final String DEFAULT_MESSAGES_DIRECTORY = "data/messages";
    public static final String DEFAULT_LOG4J_FILE = "log4j.xml";
    public static final String DEFAULT_MESSAGE_FILE_SIZE = "100";
    public static final String DEFAULT_DELAY_BETWEEN_RETRIES = "10000";
    public static final String DEFAULT_GARBAGE_COLLECTION_PERIOD = "3600000"; // once/hour
    public static final String DEFAULT_NETWORK = Networks.Mina.toString();
    public static final String DEFAULT_MAX_WRITE_FAILURES = "5";

    public static final String DEFAULT_CLUSTER_FILE = "data/cluster.json";
    public static final String DEFAULT_CLUSTER_HEALTH_CHECK_PERIOD = "86400000"; // one day
    public static final String DEFAULT_CLUSTER_TIMEOUT = "604800000"; // one week
    public static final String DEFAULT_CLUSTER_PORT = "6789";

    public static final String DEFAULT_PANIC_LIMIT = "3";
    public static final String DEFAULT_PANIC_TIMEOUT = "3600000"; // one hour

    public static final String DEFAULT_ENCRYPION_MODE = "localCA";
    public static final String DEFAULT_TRUST_STORE = "truststore";
    public static final String DEFAULT_TRUST_STORE_ALIAS = "ca";
    public static final String DEFAULT_KEYSTORE = "serverkeystore";
    public static final String DEFAULT_KEYSTORE_ALIAS = "server";
    public static final String DEFAULT_CERTIFICATE_ALIAS = "server";

    public static final String DEFAULT_HTTP_BASE = "html";
    public static final String DEFAULT_HTTP_SSL_PORT = "443";
    public static final String DEFAULT_HTTP_PORT = "80";
    public static final String DEFAULT_HTTP_SERVER = "jetty";


    public static String[][] DEFAULT_PROPERTIES = {
            {PROPERTY_USERS_FILE, DEFAULT_USERS_FILE},
            {PROPERTY_TOPICS_FILE, DEFAULT_TOPICS_FILE},
            {PROPERTY_SUBSCRIPTIONS_FILE, DEFAULT_SUBSCRIPTIONS_FILE},
            {PROPERTY_MESSAGE_PORT, DEFAULT_MESSAGE_PORT},
            {PROPERTY_MESSAGES_DIRECTORY, DEFAULT_MESSAGES_DIRECTORY},
            {PROPERTY_DELIVERY_DIRECTORY, DEFAULT_DELIVERY_DIRECTORY},
            {PROPERTY_LOG4J_FILE, DEFAULT_LOG4J_FILE},
            {PROPERTY_MESSAGE_FILE_SIZE, DEFAULT_MESSAGE_FILE_SIZE},
            {PROPERTY_FILE_CHECK_PERIOD, DEFAULT_FILE_CHECK_PERIOD},
            {PROPERTY_PROPERTIES_FILE, DEFAULT_PROPERTIES_FILENAME},
            {PROPERTY_NETWORK, DEFAULT_NETWORK},
            {PROPERTY_MAX_WRITE_FAILURES, DEFAULT_MAX_WRITE_FAILURES},

            {PROPERTY_ENCRYPTION_MODE, DEFAULT_ENCRYPION_MODE},
            {PROPERTY_TRUST_STORE, DEFAULT_TRUST_STORE},
            {PROPERTY_TRUST_STORE_ALIAS, DEFAULT_TRUST_STORE_ALIAS},
            {PROPERTY_KEYSTORE, DEFAULT_KEYSTORE},
            {PROPERTY_KEYSTORE_ALIAS, DEFAULT_KEYSTORE_ALIAS},
            {PROPERTY_CERTIFICATE_ALIAS, DEFAULT_CERTIFICATE_ALIAS},

            {PROPERTY_CLUSTER_FILE, DEFAULT_CLUSTER_FILE},
            {PROPERTY_CLUSTER_HEALTH_CHECK_PERIOD, DEFAULT_CLUSTER_HEALTH_CHECK_PERIOD},
            {PROPERTY_CLUSTER_PORT, DEFAULT_CLUSTER_PORT},
            {PROPERTY_CLUSTER_TIMEOUT, DEFAULT_CLUSTER_TIMEOUT},

            {PROPERTY_DELAY_BETWEEN_RETRIES, DEFAULT_DELAY_BETWEEN_RETRIES},
            {PROPERTY_GARBAGE_COLLECTION_PERIOD, DEFAULT_GARBAGE_COLLECTION_PERIOD},

            {PROPERTY_HTTP_PORT, DEFAULT_HTTP_PORT},
            {PROPERTY_HTTP_SSL_PORT, DEFAULT_HTTP_SSL_PORT},
            {PROPERTY_HTTP_BASE, DEFAULT_HTTP_BASE},
            {PROPERTY_HTTP_SERVER, DEFAULT_HTTP_SERVER},

            {PROPERTY_PANIC_LIMIT, DEFAULT_PANIC_LIMIT},
            {PROPERTY_PANIC_TIMEOUT, DEFAULT_PANIC_TIMEOUT},
    };

    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public MirandaProperties (String filename, Writer writer) {
        super(filename, writer);

        properties = new Properties();
        load();

        MirandaPropertiesReadyState mirandaPropertiesReadyState = new MirandaPropertiesReadyState(this);
        setCurrentState(mirandaPropertiesReadyState);
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

    public void load (MirandaCommandLine mirandaCommandLine) {
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
        temp = mirandaCommandLine.asProperties();
        PropertiesUtils.overwrite(properties, temp);

        this.properties = properties;
    }


    public void updateSystemProperties()
    {
        PropertiesUtils.overwrite(System.getProperties(), asProperties());
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

    public EncryptionModes getEncryptionModeProperty (String name) {
        String value = getProperty(name);

        EncryptionModes mode = EncryptionModes.Unknown;

        if (null == value) {
            mode = EncryptionModes.Unknown;
        } else if (value.equalsIgnoreCase("none") || value.equalsIgnoreCase("off") || value.equalsIgnoreCase("nossl")) {
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

    public WebSevers getHttpServerProperty (String name) {
        String value = getProperty(name);
        value.trim();
        WebSevers webServer = WebSevers.Jetty;

        if (value.equalsIgnoreCase("netty"))
            webServer = WebSevers.Netty;

        return webServer;
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

    public Properties asProperties () {
        Properties temp = new Properties(properties);
        return temp;
    }

    public void load () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);

        load(commandLine);
    }

    public void setProperties (Properties properties) {
        this.properties = properties;
    }

    public List<Property> asPropertyList () {
        Properties properties = asProperties();
        List<Property> list = new ArrayList<Property>(properties.size());
        for (String name : properties.stringPropertyNames()) {
            String value = properties.getProperty(name);
            Property property = new Property(name, value);
            list.add(property);
        }

        return list;
    }

    public void write () {
        OutputStreamWriter outputStreamWriter = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);

            getProperties().store(outputStreamWriter, null);

            outputStreamWriter.close();
            byteArrayOutputStream.close();

            byte[] data = byteArrayOutputStream.toByteArray();
            getWriter().sendWrite(getQueue(), this, getFilename(), data);
        } catch (IOException e) {
            Panic panic = new Panic("Exception trying to write properties file", e, Panic.Reasons.ExceptionWritingFile);
            Miranda.getInstance().panic(panic);
        } finally {
            Utils.closeIgnoreExceptions(outputStreamWriter);
        }
    }
}
