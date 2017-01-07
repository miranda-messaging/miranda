package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.ltsllc.miranda.main.Startup;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Properties;
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


    private Miranda() {
        super ("miranda");
        State s = new Startup(this);
        setCurrentState(s);
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        setQueue(queue);
    }


    public synchronized static Miranda getInstance() {
        if (null == ourInstance)
        {
            ourInstance = new Miranda();
        }

        return ourInstance;
    }


    public static void main(String[] argv) {
        logger.info ("Starting");
        getInstance().setArguments(argv);
        getInstance().start();
    }

    private void setArguments (String[] argv) {
        Startup s = (Startup) getCurrentState();
        s.setArguments(argv);
    }
}
