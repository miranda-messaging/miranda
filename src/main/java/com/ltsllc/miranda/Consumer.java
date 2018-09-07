/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Mergeable;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.miranda.states.ShuttingDownState;
import com.ltsllc.miranda.panics.ExceptionPanic;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.panics.StartupPanic;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * An object that
 */
public class Consumer extends Subsystem implements Comparer, Mergeable {
    private static Logger logger = Logger.getLogger(Consumer.class);
    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            gson = gsonBuilder.create();
        }

        return gson;
    }

    public static void setGson(Gson gson) {
        Consumer.gson = gson;
    }

    public static void setLogger(Logger logger) {
        Consumer.logger = logger;
    }

    public static Logger getLogger() {
        return Consumer.logger;
    }

    private transient State currentState;
    private long lastChange = -1;

    public long getLastChange() {
        return lastChange;
    }

    public void setLastChange(long lastChange) {
        this.lastChange = lastChange;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State s) {
        if (null == currentState)
            logger.debug(this + " transitioning from null to " + s);
        else if (null == s)
            logger.debug(this  + " transitioning to null");
        else if (currentState.getClass() != s.getClass())
            logger.debug(this + " transitioning to " + s);

        State nextState = s;

        if (nextState == s)
            currentState = nextState;
        else {
            logger.debug("The start method in " + s + " transitioned to " + nextState + " for " + this);
            currentState = nextState;
        }
    }

    public Consumer(String name) {
        basicConstructor(name);
    }

    public Consumer(String name, BlockingQueue<Message> queue) {
        basicConstructor(name, queue);
    }

    public Consumer() {
    }

    public void basicConstructor(String name) {
        super.basicConstructor(name, null);
    }


    public State startCurrentState() {
        State current = getCurrentState();

        if (null == current) {
            Panic panic = new Panic("Null start state", null, Panic.Reasons.NullCurrentState);
            Miranda.getInstance().panic(panic);
        } else {
            try {
                State nextState = current.start();
                return nextState;
            } catch (Throwable t) {
                Panic panic = new StartupPanic("Exception in start", t, StartupPanic.StartupReasons.ExceptionInStart);
                Miranda.getInstance().panic(panic);
            }
        }

        return current;
    }

    public void exitCurrentState() {
        State current = getCurrentState();

        if (null == current) {
            // do nothing
        } else {
            try {
                current.exit();
            } catch (Throwable t) {
                Panic panic = new Panic("Exception in exit", t, Panic.Reasons.ExceptionInExit);
                Miranda.getInstance().panic(panic);
            }
        }
    }

    public State processMessageInCurrentState(Message message) throws MirandaException {
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


        while (nextState != getCurrentState()) {
            exitCurrentState();
            setCurrentState(nextState);
            nextState = startCurrentState();
        }

        logger.debug("Consumer " + this + " starting");
        setCurrentState(nextState);

        State stop = StopState.getInstance();

        if (stop == currentState) {
            int i = 1;
            i++;
        }

        while (nextState != stop && !Miranda.panicking && !getStopped()) {
            Message m = getNextMessage();

            try {
                if (null != m) {
                    State current = currentState;
                    Message message = m;
                    if (currentState.getClass() == ShuttingDownState.class && m.getClass() == ShutdownResponseMessage.class) {
                        logger.debug("In shutting down state");
                    }

                    logger.debug(this + " received " + m + " from " + m.getSenderObject());
                    nextState = processMessageInCurrentState(m);
                    if (currentState.getClass() == ShuttingDownState.class && nextState.getClass() != ShuttingDownState.class) {
                        logger.debug("transitioning out of ShuttingDownState", new Exception());
                        logger.debug(m);
                    }


                    if (currentState.getOverideState() != null) {
                        nextState = currentState.getOverideState();
                        currentState.setOverideState(null);
                    }

                    if (current != nextState) {
                        exitCurrentState();

                       if (nextState != null) {
                            nextState.start();
                        }
                    }
                    setCurrentState(nextState);
                }
            } catch (Throwable e) {
                ExceptionPanic panic = new ExceptionPanic(e, Panic.Reasons.ExceptionInProcessMessage);
                Miranda.panicMiranda(panic);
            }

        }

        if (Miranda.panicking) {
            logger.error(this + " is terminating due to a panic");
        } else {
            logger.info(this + " terminating");
        }
    }



    /**
     * Process the next message.
     * <p>
     * The defualt implementation ignores the message and returns {@link #getCurrentState()}.
     *
     * @param m The message to prcess
     * @return The next state.
     */
    public State processMessage(Message m) throws MirandaException {
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
        logger.info(this + " is sending " + m);
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
        logger.debug (message.getSenderObject() + " sent " + message + " to " + this);
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

    public void sendShutdownResponse(BlockingQueue<Message> senderQueue, Object sender, String name) {
        ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(senderQueue, sender, name);
        sendToMe(shutdownResponseMessage);
    }

    public void shutdown() throws MirandaException {
        logger.info(this + " shutting down.");
        setCurrentState(StopState.getInstance());
    }

    public boolean merge (Mergeable mergeable) {
        if (getLastChange() > mergeable.getLastChange())
            return false;
        else {
            copyFrom(mergeable);
            return true;
        }
    }

    public void copyFrom (Mergeable mergeable) {
        Consumer other = (Consumer) mergeable;

        setCurrentState(other.getCurrentState());
        setLastChange(other.getLastChange());
    }

    public String toString() {
        return getName() + "@" + getCurrentState();
    }

    public String toJson () {
        return getGson().toJson(this);
    }
}
