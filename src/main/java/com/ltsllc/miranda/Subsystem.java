package com.ltsllc.miranda;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Miranda subsystem.
 *
 * <H2>Attributes</H2>
 * <UL>
 *     <LI>map - unlike most other attributes</LI>
 *     <LI>name - the name of the subsystem</LI>
 *     <LI>queue - the queue that the subsystem gets messages from.</LI>
 *     <LI>thre - the thread the subystem is running in.</LI>
 * </UL>
 *
 * <H2>Methods</H2>
 * <UL>
 *     <LI></LI>
 * </UL>
 * Created by Clark on 12/30/2016.
 */
public abstract class Subsystem implements Runnable {
    private static Map<String, BlockingQueue<Message>> ourMap = new HashMap<String, BlockingQueue<Message>>();

    public static synchronized void register (String name, BlockingQueue<Message> queue)
    {
        ourMap.put(name, queue);
    }
    public static synchronized void unregister (String name)
    {
        ourMap.remove(name);
    }
    public synchronized BlockingQueue<Message> find (String name) {
        return ourMap.get(name);
    }

    static private Logger logger = Logger.getLogger("Subsytem");

    private String name;
    private BlockingQueue<Message> queue;
    private Thread thread;

    public String getName () {
        return name;
    }

    public BlockingQueue<Message> getQueue () {
        return queue;
    }
    public void setQueue (BlockingQueue<Message> newQueue) {
        queue = newQueue;
    }

    public Thread getThread() {
        return thread;
    }


    public Subsystem (String name) {
        this.name = name;
        this.queue = new LinkedBlockingQueue<Message>();
    }

    /**
     * Start the subsystem.
     *
     * <P>
     *     This version of the method starts a new thread.
     * </P>
     */
    public State start() {
        thread = new Thread(this, getName());
        thread.start();
        return StartState.getInstance();
    }



}
