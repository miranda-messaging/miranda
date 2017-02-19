package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StartState;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.ClusterFile;
import com.ltsllc.miranda.file.FileWatcher;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.file.SystemDeliveriesFile;
import com.ltsllc.miranda.messagesFile.SystemMessages;
import com.ltsllc.miranda.server.HttpServer;
import com.ltsllc.miranda.timer.MirandaTimer;
import com.ltsllc.miranda.user.PostHandler;
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
    private static FileWatcher ourFileWatcher;
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

    public static void initialize ()
    {
        StartState.initialize();

        ourInstance = new Miranda();
        ourFileWatcher = new FileWatcher();
        timer = new MirandaTimer();
        timer.start();
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public void setHttpServer(HttpServer httpServer) {
        this.httpServer = httpServer;
    }

    public static FileWatcher getFileWatcher() {
        return ourFileWatcher;
    }

    private void setArguments (String[] argv) {
        Startup s = (Startup) getCurrentState();
        s.setArguments(argv);
    }

    public static FileWatcher getWatchService() {
        return ourFileWatcher;
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
