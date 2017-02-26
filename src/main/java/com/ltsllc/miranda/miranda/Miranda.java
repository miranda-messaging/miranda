package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StartState;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.server.HttpServer;
import com.ltsllc.miranda.timer.MirandaTimer;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The Miranda system.
 *
 * This is also the main class for the system.
 *
 * Created by Clark on 12/30/2016.
 */
public class Miranda extends Consumer {
    private static Logger logger = Logger.getLogger(Miranda.class);
    private static Miranda ourInstance;
    public static FileWatcherService fileWatcher;
    public static MirandaTimer timer;

    private HttpServer httpServer;
    private SystemMessages systemMessages;
    private SystemDeliveriesFile deliveriesFile;


    private Miranda() {
        super ("miranda");
        State s = new Startup(this);
        setCurrentState(s);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
    }


    public synchronized static Miranda getInstance() {
        return ourInstance;
    }

    public static void main(String[] argv) {
        logger.info ("Starting");
        initialize();
        getInstance().setArguments(argv);
        getInstance().start();
    }

    public SystemMessages getSystemMessages() {
        return systemMessages;
    }

    public SystemDeliveriesFile getDeliveriesFile() {
        return deliveriesFile;
    }

    public void setDeliveriesFile(SystemDeliveriesFile deliveriesFile) {
        this.deliveriesFile = deliveriesFile;
    }

    public void setSystemMessages(SystemMessages systemMessages) {
        this.systemMessages = systemMessages;
    }

    public static synchronized void initialize ()
    {
        if (null == ourInstance) {

            StartState.initialize();

            MirandaProperties properties = MirandaProperties.getInstance();

            fileWatcher = new FileWatcherService(properties.getIntegerProperty(MirandaProperties.PROPERTY_FILE_CHECK_PERIOD));
            fileWatcher.start();

            timer = new MirandaTimer();
            timer.start();

            ourInstance = new Miranda();
        }
    }

    public static synchronized void reset () {
        ourInstance = null;
        fileWatcher = null;
        timer = null;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    private void setArguments (String[] argv) {
        Startup s = (Startup) getCurrentState();
        s.setArguments(argv);
    }

    public static void performGarbageCollection() {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(Miranda.getInstance().getQueue(),
                Miranda.getInstance());

        Miranda.getInstance().getCurrentState().processMessage(garbageCollectionMessage);
    }

    public static void registerPostHandler(String path, BlockingQueue<Message> handlerQueue) {
        Miranda miranda = Miranda.getInstance();
        HttpServer httpServer = miranda.getHttpServer();
        httpServer.registerPostHandler(path, handlerQueue);
    }
}
