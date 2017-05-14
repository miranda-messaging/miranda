package com.ltsllc.miranda;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
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
    private List<Message> deferredQueue;

    public State () {
        this.deferredQueue = new LinkedList<Message>();
    }

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
            Panic panic = new Panic("Interrupted while trying to send message", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
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
        State nextState = getContainer().getCurrentState();

        switch (m.getSubject()) {
            case Stop: {
                StopMessage stopMessage = (StopMessage) m;
                nextState = processStopMessage (stopMessage);
                break;
            }

            default : {
                String message = getContainer() + " in state " + getContainer().getCurrentState() + " does not understand " + m;
                logger.error(message);
                logger.error("Message created at", m.getWhere());
                Panic panic = new Panic(message, null, Panic.Reasons.DoesNotUnderstand);
                Miranda.getInstance().panic(panic);
            }
        }

        return nextState;
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

    public State processStopMessage(StopMessage stopMessage) {
        getContainer().stop();

        StopState stopState = StopState.getInstance();
        return stopState;
    }

    public State ignore (Message message) {
        logger.info (this + " ignoring " + message);

        return getContainer().getCurrentState();
    }

    public State defer (Message message) {
        deferredQueue.add(message);

        return getContainer().getCurrentState();
    }
}
