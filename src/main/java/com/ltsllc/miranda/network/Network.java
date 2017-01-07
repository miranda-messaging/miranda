package com.ltsllc.miranda.network;

import com.ltsllc.miranda.*;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * The network interface.
 * <p>
 * This class abstracts away the network interface so that it sends messages like
 * "GotConnection" to the rest of the system.  This the object that handle new
 * messages.
 * <p>
 * Created by Clark on 1/2/2017.
 */
public class Network extends Subsystem {
    private static Logger logger = Logger.getLogger(Network.class);

    private State state;

    public State getState () {
        return state;
    }

    public void setState (State s) {
        state = s;
    }

    public Network(BlockingQueue<Message> queue) {
        super("Network");
        setQueue(queue);
    }

    public Message nextMessage () {
        try {
            return getQueue().take();
        } catch (InterruptedException e) {
            logger.warn("Exception thrown while trying to get next message", e);
            return null;
        }
    }

    public void run () {
        logger.info (getName() + " starting");

        StopState stop = StopState.getInstance();
        State nextState = getState();

        while (stop != nextState) {
            Message m = nextMessage();
            nextState = processMessage(m);
        }

        logger.info (getName() + " terminating.");
    }

    private State processMessage (Message m) {
        logger.warn ("received " + m);
        return getState();
    }

}
