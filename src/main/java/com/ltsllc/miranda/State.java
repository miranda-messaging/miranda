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

    private BlockingQueue<Message> myQueue;
    private Consumer container;

    public State(BlockingQueue<Message> queue) {
        myQueue = queue;
    }

    public BlockingQueue<Message> getQueue() {
        return myQueue;
    }

    public State (Consumer container) {
        this.container = container;
    }

    /**
     * Process messages.
     *
     * This method processes the message in the queue returned by {@link #getQueue()}, waiting if the queue is empty.
     * The method processes messages until the next state, as returned by {@link #processMessage(Message)} is not
     * this.
     *
     * @return The next state.
     * @throws InterruptedException If the thread is interrupted while waiting for a message.
     */
    public State process () throws InterruptedException {
        logger.info("Starting");

        State nextState = this;
        while (nextState == this) {
            Message m = getQueue().take();
            nextState = processMessage(m);
        }

        return nextState;
    }

    /**
     * Process the next message and return the next state.
     *
     * The default implementation is to ignore the message and return this;
     *
     * @return The next state.  Default behavior is to return this.
     */
    public State processMessage (Message m) {
        return this;
    }

    public State start ()
    {
        logger.info ("starting");
        return this;
    }

    public void send (BlockingQueue<Message> queue, Message m) {
        try {
            queue.put(m);
        } catch (InterruptedException e) {
            logger.warn ("Interrupted while sending message", e);
        }
    }
}
