package com.ltsllc.miranda.main;

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.ConnectMessage;
import com.ltsllc.miranda.file.SubscriptionsFile;
import com.ltsllc.miranda.file.TopicFile;
import com.ltsllc.miranda.file.UsersFile;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.util.IOUtils;
import com.ltsllc.miranda.util.PropertiesUtils;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.ltsllc.miranda.MirandaProperties.DEFAULT_PROPERTIES;
import static com.ltsllc.miranda.MirandaProperties.PROPERTY_CLUSTER_FILE;

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
    public static final String DEBUG_FLAG = "-debug";
    public static final String LOG4J_FILE = "log4j.xml";

    private static Logger logger = Logger.getLogger(Startup.class);

    private String[] arguments;
    private BlockingQueue<Message> clusterQueue;
    private int index;

    public int getIndex () {
        return index;
    }

    public void setIndex (int i) {
        index = i;
    }

    public BlockingQueue<Message> getClusterQueue() {
        return clusterQueue;
    }

    public void setClusterQueue (BlockingQueue<Message> queue) {
        clusterQueue = queue;
    }

    public Startup (Consumer container) {
        super(container);
    }

    public Startup(BlockingQueue<Message> queue, String[] argv) {
        super(queue);
        arguments = argv;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments (String[] argv) {
        arguments = argv;
    }

    public State start() {
        processArguments();
        loadProperties();
        startSubsystems();
        send(getClusterQueue(), new ConnectMessage());
        return new Ready(getQueue());
    }

    public void processArguments ()
    {
        DOMConfigurator.configure("log4j.xml");

        if (null != getArguments()) {
            for (String s : getArguments()) {
                if (s.equals(DEBUG_FLAG)) {
                    logger.setLevel(Level.DEBUG);
                }

                setIndex(getIndex() + 1);
            }
        }
    }


    /**
     * Load the system properties; using the defaults if nothing else is available.
     * <p>
     * The method will use the first argument passed to it, if supplied,
     * otherwise it will use {@link MirandaProperties#PROPERTY_SYSTEM_PROPERTIES if that is defined.
     * If neither is defined then it
     * will look for {@link MirandaProperties#DEFAULT_PROPERTIES_FILENAME} in the current directory.
     *
     * <p>
     * The method always "augments" the system properties with the {@link #DEFAULT_PROPERTIES}
     * if it finds a proerties file it will augment the result with that
     * file.
     */
    private void loadProperties () {
        Properties systemProperties = System.getProperties();
        String filename = MirandaProperties.DEFAULT_PROPERTIES_FILENAME;

        if (getArguments().length > 0) {
            filename = getArguments()[0];
        } else if (systemProperties.getProperty(MirandaProperties.PROPERTY_SYSTEM_PROPERTIES) != null) {
            filename = systemProperties.getProperty(MirandaProperties.PROPERTY_SYSTEM_PROPERTIES);
        }

        Properties defaults = PropertiesUtils.buildFrom(DEFAULT_PROPERTIES);
        PropertiesUtils.augment(systemProperties, defaults);

        logger.info("Propertie file = " + filename);

        File systemPropertiesFile = new File(filename);
        if (systemPropertiesFile.exists()) {
            FileReader fin = null;
            try {
                fin = new FileReader(filename);

                Properties p = new Properties();
                p.load(fin);
                PropertiesUtils.augment(systemProperties, p);

            } catch (IOException e) {
                System.err.println("error reading properties file " + filename);
                System.err.println(e);
                System.exit(1);
            } finally {
                IOUtils.closeNoExceptions(fin);
            }
        }

        logProperties(systemProperties);
    }

    private void logProperties (Properties p)
    {
        Object[] names = p.stringPropertyNames().toArray();
        Arrays.sort(names);

        for (Object o : names) {
            String name = (String) o;
            String value = p.getProperty(name);
            logger.info(name + " = " + value);
        }
    }


    /**
     * Start the Miranda subsystems.
     *
     * <P>
     *     This method starts the {@link Writer}, the {@link Cluster} and the
     *     {@link Network} object.  The Cluster may start additional subsystems
     *     for the Nodes in the Cluster.
     * </P>
     */
    private void startSubsystems() {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        Writer w = new Writer(queue);
        w.start();

        queue = new LinkedBlockingQueue<Message>();
        Cluster c = new Cluster(System.getProperty(PROPERTY_CLUSTER_FILE));
        c.start (queue);
        setClusterQueue(queue);

        queue = new LinkedBlockingQueue<Message>();
        Network n = new Network(queue);
        n.start();
    }

    private void loadFiles(BlockingQueue<Message> queue) {
        Properties p = System.getProperties();

        String filename = p.getProperty(MirandaProperties.PROPERTY_USERS_FILE);
        UsersFile users = new UsersFile(queue, filename);

        filename = p.getProperty(MirandaProperties.PROPERTY_TOPICS_FILE);
        TopicFile topics = new TopicFile(queue, filename);

        filename = p.getProperty(MirandaProperties.PROPERTY_SUBSCRIPTIONS_FILE);
        SubscriptionsFile subscriptions = new SubscriptionsFile(queue, filename);

        String directoryName = p.getProperty(MirandaProperties.PROPERTY_MESSAGES_DIRECTORY);
        directoryName = p.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);
    }


}
