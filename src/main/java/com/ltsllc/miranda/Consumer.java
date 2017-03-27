package com.ltsllc.miranda;

import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.StopMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/1/2017.
 */
public class Consumer extends Subsystem implements Comparer {
    private static Logger logger = Logger.getLogger(Consumer.class);

    public static void setLogger(Logger logger) {
        Consumer.logger = logger;
    }

    private State currentState;

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State s) {
        if (null == currentState)
            logger.info(this + " transitioning from null to " + s);
        else if (null == s)
            logger.info(this + " in state " + currentState + " transitioning to null");
        else if (currentState.getClass() != s.getClass())
            logger.info(this + " in state " + currentState + " transitioning to " + s);

        State nextState = s;

        if (nextState == s)
            currentState = nextState;
        else {
            logger.info("The start method in " + s + " transitioned to " + nextState + " for " + this);
            currentState = nextState;
        }
    }

    public Consumer(String name) {
        super(name);
    }

    public Consumer(String name, BlockingQueue<Message> queue) {
        super(name, queue);
    }


    public State startCurrentState() {
        State current = getCurrentState();

        if (null == current) {
            Panic panic = new StartupPanic("Null start state", null, StartupPanic.StartupReasons.NullStartState);
            Miranda.getInstance().panic(panic);
        } else {
            try {
                State nextState = current.start();
                return nextState;
            } catch (Throwable t) {
                Panic panic = new StartupPanic("Unchececked exception in start", t, StartupPanic.StartupReasons.ExceptionInStart);
                Miranda.getInstance().panic(panic);
            }
        }

        return current;
    }

    public State processMessageInCurrentState(Message message) {
        try {
            State nextState = processMessage(message);
            return nextState;
        } catch (Throwable t) {
            Panic panic = new Panic("Unchecked exception in processMessage", t, Panic.Reasons.ExceptionInProcessMessage);
            Miranda.getInstance().panic(panic);
        }

        return getCurrentState();
    }


    /**
     * run a Consumer.
     * <p>
     * <p>
     * This method implements {@link Runnable#run()}.
     * </p>
     * <p>
     * This method simply takes the next message off the object's queue
     * and processes it.  By default, the method sotps when the next
     * State is an instance of {@link StopState}.
     * </p>
     */
    public void run() {
        State nextState = startCurrentState();
        State stop = StopState.getInstance();
        logger.info(this + " starting");

        while (nextState != stop && !Miranda.panicking && nextState != null) {
            State currentState = getCurrentState();
            setCurrentState(nextState);
            if (currentState != nextState) {
                setCurrentState(nextState.start());
            }

            Message m = getNextMessage();
            if (null != m) {
                logger.info(this + " in state " + getCurrentState() + " received " + m);
                nextState = processMessageInCurrentState(m);
            }
        }

        if (Miranda.panicking) {
            logger.error(this + " is terminating due to a panic");
        } else {
            logger.info(this + " terminating");
        }
    }

    public Message getNextMessage() {
        Message nextMessage = null;
        boolean keepWaiting = true;

        while (null == nextMessage && keepWaiting) {
            try {
                nextMessage = getQueue().take();
            } catch (InterruptedException e) {
                String message = "Interupted while waiting for next message";
                logger.warn(message, e);

                Panic panic = new Panic(message, e, Panic.Reasons.ExceptionGettingNextMessage);
                if (Miranda.getInstance().panic(panic))
                    keepWaiting = false;
            }
        }

        return nextMessage;
    }

    /**
     * Process the next message.
     * <p>
     * The defualt implementation ignores the message and returns {@link #getCurrentState()}.
     *
     * @param m The message to prcess
     * @return The next state.
     */
    public State processMessage(Message m) {
        State currentState = getCurrentState();

        if (null == currentState) {
            Panic panic = new Panic("Null current state", null, Panic.Reasons.NullCurrentState);
            Miranda.getInstance().panic(panic);
        } else {
            try {
                State nextState = currentState.processMessage(m);
                return nextState;
            } catch (Throwable t) {
                Panic panic = new Panic("Unchecked exception in processMessage", t, Panic.Reasons.ExceptionInProcessMessage);
                Miranda.getInstance().panic(panic);
            }
        }

        return getCurrentState();
    }


    public void send(Message m, BlockingQueue<Message> queue) {
        logger.info(this + " in state " + getCurrentState() + " is sending " + m);
        try {
            queue.put(m);
        } catch (InterruptedException e) {
            logger.info("Exception trying to sendToMe message", e);
        }
    }


    public static void staticSend(Message m, BlockingQueue<Message> queue) {
        logger.info("Sending " + m);
        try {
            queue.put(m);
        } catch (InterruptedException e) {
            logger.info("Exception trying to sendToMe message", e);
        }
    }

    /**
     * Send if the Consumer is non-null.
     *
     * @param m
     * @param consumer
     */
    public void sendIfNotNull(Message m, Consumer consumer) {
        if (null != consumer) {
            consumer.sendToMe(m);
        }
    }

    /**
     * Send a message if the queue is non-null
     *
     * @param m
     * @param queue
     */
    public void sendIfNotNull(Message m, BlockingQueue<Message> queue) {
        if (queue != null) {
            send(m, queue);
        }
    }

    /**
     * Send a message to this object.
     */
    public void sendToMe(Message message) {
        try {
            getQueue().put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception trying to sendToMe message", e, Panic.Reasons.ExceptionSendingMessage);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (null == o || !(o instanceof Consumer))
            return false;

        Map<Object, Boolean> map = new HashMap<Object, Boolean>();

        return compare(map, o);
    }


    public boolean compare(Map<Object, Boolean> map, Object o) {
        if (map.containsKey(o))
            return map.get(o).booleanValue();

        map.put(o, new Boolean(true));

        if (this == o)
            return true;

        if (null == o || !(o instanceof Consumer)) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        Consumer other = (Consumer) o;
        return getCurrentState().compare(map, other.getCurrentState());
    }

    public void setCurrentStateWithoutStart(State state) {
        currentState = state;
    }

    public void sendStop(BlockingQueue<Message> senderQueue, Object sender) {
        StopMessage stopMessage = new StopMessage(senderQueue, sender);
        sendToMe(stopMessage);
    }

    public void stop() {
        logger.info(this + " stopping.");
        setCurrentState(StopState.getInstance());
    }

    public void sendShutdown(BlockingQueue<Message> senderQueue, Object sender) {
        ShutdownMessage shutdownMessage = new ShutdownMessage(senderQueue, sender);
        sendToMe(shutdownMessage);
    }

    public void shutdown() {
        logger.info(this + " shutting down.");
        setCurrentState(StopState.getInstance());
    }
}
