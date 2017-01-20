package com.ltsllc.miranda;

import com.ltsllc.miranda.file.FileWatcher;
import com.ltsllc.miranda.main.Startup;
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

    public static void initialize ()
    {
        ourInstance = new Miranda();
        ourFileWatcher = new FileWatcher();
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
}
