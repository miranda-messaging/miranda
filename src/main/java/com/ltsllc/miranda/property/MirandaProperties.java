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

package com.ltsllc.miranda.property;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.common.util.PropertiesUtils;
import com.ltsllc.common.util.Property;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
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
public class MirandaProperties {
    public static final String NAME = "miranda properties";

    public static final String ONE_HOUR = new Long(1000 * 60 * 60).toString();
    public static final String ONE_MINUTE = (new Long(1000 * 60)).toString();
    public static final String ONE_DAY = (new Long(1000 * 60 * 60 * 24)).toString();
    public static final String ONE_WEEK = (new Long(1000 * 60 * 60 * 24 * 7)).toString();
    public static final String ONE_MONTH = (new Long(1000 * 60 * 60 * 24 * 30)).toString();
    public static final String ONE_YEAR = (new Long(1000 * 60 * 60 * 24 *365)).toString();

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

    public static final String SESSION_PACKAGE = PACKAGE_NAME + "session.";

    public static final String SERVLET_PACKAGE = PACKAGE_NAME + "servlet.";

    public static final String KEYSTORE_PACKAGE = PACKAGE_NAME + "keystore.";

    public static final String EVENT_PACKAGE = PACKAGE_NAME + "event.";

    public static final String DELIVERY_PACKAGE = PACKAGE_NAME + "delivery,";

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

    public static final String PROPERTY_CERTIFICATE_ALIAS = ENCRYPTION_PACKAGE + "CertificateAlias";
    public static final String PROPERTY_ENCRYPTION_MODE = ENCRYPTION_PACKAGE + "Mode";

    public static final String PROPERTY_PANIC_LIMIT = PANIC_PACKAGE + "Limit";
    public static final String PROPERTY_PANIC_TIMEOUT = PANIC_PACKAGE + "Timeout";

    public static final String PROPERTY_HTTP_SSL_PORT = HTTP_PACKAGE_NAME + "SslPort";
    public static final String PROPERTY_HTTP_PORT = HTTP_PACKAGE_NAME + "HttpPort";
    public static final String PROPERTY_HTTP_BASE = HTTP_PACKAGE_NAME + "Base";
    public static final String PROPERTY_HTTP_SERVER = HTTP_PACKAGE_NAME + "Server";

    public static final String PROPERTY_TRUST_STORE_FILENAME = ENCRYPTION_PACKAGE + "Truststore";
    public static final String PROPERTY_TRUST_STORE_ALIAS = ENCRYPTION_PACKAGE + "TruststoreAlias";

    public static final String PROPERTY_MY_DNS = MY_PACKAGE + "Dns";
    public static final String PROPERTY_MY_IP = MY_PACKAGE + "Ip";
    public static final String PROPERTY_MY_PORT = MY_PACKAGE + "Port";
    public static final String PROPERTY_MY_DESCIPTION = MY_PACKAGE + "Description";

    public static final String PROPERTY_SESSION_LENGTH = SESSION_PACKAGE + "Length";
    public static final String PROPERTY_SESSION_GC_PERIOD = SESSION_PACKAGE + "GCPeriod";
    public static final String PROPERTY_SESSION_LOGIN_TIMEOUT = SESSION_PACKAGE + "LoginTimeout";

    public static final String PROPERTY_SERVLET_TIMEOUT = SERVLET_PACKAGE + "Timeout";
    public static final String PROPERTY_SERVLET_THREAD_POOL_SIZE = SERVLET_PACKAGE + "ThreadPoolSize";

    public static final String PROPERTY_KEYSTORE_FILE = KEYSTORE_PACKAGE + "File";
    public static final String PROPERTY_KEYSTORE_PRIVATE_KEY_ALIAS = KEYSTORE_PACKAGE + "PrivateKey";
    public static final String PROPERTY_KEYSTORE_CERTIFICATE_ALIAS = KEYSTORE_PACKAGE + "Certificate";
    public static final String PROPERTY_KEYSTORE_CA_ALIAS = KEYSTORE_PACKAGE + "CA";

    public static final String PROPERTY_EVENT_OBJECT_LIMIT = EVENT_PACKAGE + "ObjectLimit";
    public static final String PROPERTY_EVENT_EVICTION_PERIOD = EVENT_PACKAGE + "EvictionPeriod";

    public static final String PROPERTY_DELIVERY_OBJECT_LIMIT = DELIVERY_PACKAGE + "ObjectLimit";

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

    public static final String DEFAULT_ENCRYPTION_MODE = "localCA";
    public static final String DEFAULT_TRUST_STORE = "truststore";
    public static final String DEFAULT_TRUST_STORE_ALIAS = "ca";
    public static final String DEFAULT_CERTIFICATE_ALIAS = "server";

    public static final String DEFAULT_HTTP_BASE = "html";
    public static final String DEFAULT_HTTP_SSL_PORT = "443";
    public static final String DEFAULT_HTTP_PORT = "80";
    public static final String DEFAULT_HTTP_SERVER = "jetty";

    public static final String DEFAULT_SESSION_LENGTH = "3600000"; // one hour
    public static final String DEFAULT_SESSION_GC_PERIOD = "300000"; // 5 minutes
    public static final String DEFAULT_SESSION_LOGIN_TIMEOUT = "1000";

    public static final String DEFAULT_SERVLET_TIMEOUT = "1000";
    public static final String DEFAULT_SERVLET_THREAD_POOL_SIZE = "20";

    public static final String DEFAULT_KEYSTORE_FILE = "keystore";
    public static final String DEFAULT_KEYSTORE_PRIVATE_KEY_ALIAS = "private";
    public static final String DEFAULT_KEYSTORE_CERTIFICATE_ALIAS = "certificate";
    public static final String DEFAULT_KEYSTORE_CA_ALIAS = "ca";

    public static final String DEFAULT_EVENT_OBJECT_LIMIT = "1000000";
    public static final String DEFAULT_EVENT_EVICTION_PERIOD = ONE_HOUR;

    public static final String DEFAULT_DELIVERY_OBJECT_LIMIT = "1000000";

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

            {PROPERTY_ENCRYPTION_MODE, DEFAULT_ENCRYPTION_MODE},
            {PROPERTY_TRUST_STORE_FILENAME, DEFAULT_TRUST_STORE},
            {PROPERTY_TRUST_STORE_ALIAS, DEFAULT_TRUST_STORE_ALIAS},
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

            {PROPERTY_SESSION_LENGTH, DEFAULT_SESSION_LENGTH},
            {PROPERTY_SESSION_GC_PERIOD, DEFAULT_SESSION_GC_PERIOD},
            {PROPERTY_SESSION_LOGIN_TIMEOUT, DEFAULT_SESSION_LOGIN_TIMEOUT},

            {PROPERTY_SERVLET_TIMEOUT, DEFAULT_SERVLET_TIMEOUT},
            {PROPERTY_SERVLET_THREAD_POOL_SIZE, DEFAULT_SERVLET_THREAD_POOL_SIZE},

            {PROPERTY_KEYSTORE_FILE, DEFAULT_KEYSTORE_FILE},
            {PROPERTY_KEYSTORE_PRIVATE_KEY_ALIAS, DEFAULT_KEYSTORE_PRIVATE_KEY_ALIAS},
            {PROPERTY_KEYSTORE_CERTIFICATE_ALIAS, DEFAULT_KEYSTORE_CERTIFICATE_ALIAS},
            {PROPERTY_KEYSTORE_CA_ALIAS, DEFAULT_KEYSTORE_CA_ALIAS},

            {PROPERTY_EVENT_OBJECT_LIMIT, DEFAULT_EVENT_OBJECT_LIMIT},
            {PROPERTY_EVENT_EVICTION_PERIOD, DEFAULT_EVENT_EVICTION_PERIOD},

            {PROPERTY_DELIVERY_OBJECT_LIMIT, DEFAULT_DELIVERY_OBJECT_LIMIT}
    };

    private static MirandaProperties instance;

    private Properties properties;
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Properties getProperties() {
        return properties;
    }

    public static MirandaProperties getInstance() {
        return instance;
    }

    public static synchronized void setInstance (MirandaProperties mirandaProperties) {
        if (null != instance) {
            StartupPanic startupPanic = new StartupPanic("Attempt to create multiple instances of MirandaProperties",
                    StartupPanic.StartupReasons.MultipleProperties);
            Miranda.panicMiranda(startupPanic);
        } else {
            instance = mirandaProperties;
        }
    }

    public MirandaProperties (String filename) {
        properties = new Properties();
        this.filename = filename;

        setInstance(this);

        load();
    }

    public MirandaProperties (Properties p) {
        properties = p;
    }

    public MirandaProperties()
    {
        properties = PropertiesUtils.buildFrom(DEFAULT_PROPERTIES);
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

    public void load (MirandaCommandLine mirandaCommandLine) throws IOException {
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
        logger.info ("Properties\n" + PropertiesUtils.toString(getProperties()));
    }

    public Properties asProperties () {
        Properties temp = new Properties(properties);
        return temp;
    }

    public void load () {
        String[] argv = new String[0];
        MirandaCommandLine commandLine = new MirandaCommandLine(argv);

        try {
            load(commandLine);
        } catch (IOException e) {
            Panic panic = new StartupPanic("Exception loding Properties", e, StartupPanic.StartupReasons.ExceptionLoadingProperties);
            Miranda.getInstance().panic(panic);
        }
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
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(getFilename());
            getProperties().store(fileOutputStream, null);
        } catch (IOException e) {
            Panic panic = new Panic("Exception trying to write properties file", e, Panic.Reasons.ExceptionWritingFile);
            Miranda.getInstance().panic(panic);
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }

    public long getLongProperty (String name) throws MirandaException {
        String value = null;

        try {
            value = getProperty(name);

            if (null == value)
                throw new UndefinedPropertyException(name);

            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new InvalidPropertyException(e, name, value);
        }
    }

    public long getLongProperty (String name, String defaultValue)
    {
        String value = getProperties().getProperty(name);
        if (null == value)
            value = defaultValue;

        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            if (value != defaultValue)
                return Long.parseLong(defaultValue);
        }

        return -1;
    }

    public void remove (String property) {
        getProperties().remove(property);
    }

    public byte[] getBytes () {
        return MirandaFile.getGson().toJson(getProperties()).getBytes();
    }
}
