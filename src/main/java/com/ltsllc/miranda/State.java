package com.ltsllc.miranda;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * An object that knows how to process a {@link BlockingQueue<Message>}
 *
 * Created by Clark on 12/30/2016.
 */
public abstract class State {
    private static Logger logger = Logger.getLogger(State.class);

    private Consumer container;

    public Consumer getContainer() {
        return container;
    }

    public State (Consumer container) {
        this.container = container;
    }

    public State start ()
    {
        logger.info (getContainer() + " starting");
        return this;
    }

    public void send (BlockingQueue<Message> queue, Message m) {
        try {
            logger.info (getContainer() + " in state " + this + " sending " + m);
            queue.put(m);
        } catch (InterruptedException e) {
            logger.warn ("Interrupted while sending message", e);
        }
    }

    /**
     * Process the next message and return the next state.
     *
     * The default implementation logs a warning and returns this.
     *
     * @return The next state.  Default behavior is to return this.
     */
    public State processMessage (Message m)
    {
        IllegalStateException e = new IllegalStateException();
        logger.error(this + " does not understand " + m, e);

        logger.error ("message created at", m.getWhere());

        return StopState.getInstance();
    }
}
