package com.ltsllc.miranda;

import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * An object that knows how to process a {@link BlockingQueue<Message>}
 *
 * Created by Clark on 12/30/2016.
 */
public abstract class State {
    private static Logger logger = Logger.getLogger(State.class);

    private Consumer container;
    private boolean started = false;

    public boolean stated () {
        return started;
    }

    public void setStarted (boolean started) {
        this.started = started;
    }

    public Consumer getContainer() {
        return container;
    }

    public State (Consumer container) {
        this.container = container;
    }

    public State start ()
    {
        setStarted(true);
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

        Panic panic = new Panic(this + " does not understand " + m, e, Panic.Reasons.DoesNotUnderstand);
        if (Miranda.getInstance().panic(panic)) {
            return StopState.getInstance();
        } else {
            return this;
        }
    }


    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o == this)
            return true;

        if (!(o instanceof State))
            return false;

        State other = (State) o;

        return getContainer().equals(other.getContainer());
    }


    public boolean compare (Map<Object, Boolean> map, Object o) {
        if (map.containsKey(o)) {
            return map.get(o).booleanValue();
        }

        if (null == o || !(o instanceof  State))
        {
            map.put(o, Boolean.FALSE);
            return false;
        }

        State other = (State) o;
        return getContainer().compare(map, other.getContainer());
    }
}
