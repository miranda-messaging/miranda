package com.ltsllc.miranda.miranda;

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.commadline.MirandaCommandLine;
import com.ltsllc.miranda.file.FileWatcherService;
import com.ltsllc.miranda.deliveries.SystemDeliveriesFile;
import com.ltsllc.miranda.event.SystemMessages;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.netty.NettyHttpServer;
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
    public static MirandaProperties properties;
    public static MirandaFactory factory;
    public static MirandaCommandLine commandLine;
    public static boolean panicing = false;

    private HttpServer httpServer;
    private SystemMessages systemMessages;
    private SystemDeliveriesFile deliveriesFile;


    public Miranda (String[] argv) {
        super ("miranda");

        ourInstance = this;

        State s = new Startup(this, argv);
        setCurrentState(s);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
    }


    public synchronized static Miranda getInstance() {
        return ourInstance;
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
        boolean keepGoing = false;

        logger.fatal ("System terminating due to a panic", panic);

        if (!keepGoing) {
            System.exit(1);
        }

        return keepGoing;
    }

    public static void main(String[] argv) {
        logger.info ("Starting");
        Miranda miranda = new Miranda(argv);
        miranda.start();
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
/*
    public static void registerPostHandler(String path, BlockingQueue<Message> handlerQueue) {
        Miranda miranda = Miranda.getInstance();
        HttpServer httpServer = miranda.getHttpServer();
        httpServer.registerPostHandler(path, handlerQueue);
    }
    */
}
