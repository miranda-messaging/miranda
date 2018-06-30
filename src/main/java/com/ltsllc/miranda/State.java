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

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.node.networkMessages.NetworkMessage;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.panics.ShutdownPanic;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.shutdown.ShutdownResponseMessage;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * An object that knows how to process a {@link BlockingQueue< Message >}
 * <p>
 * Created by Clark on 12/30/2016.
 */
public abstract class State {
    private static Logger logger = Logger.getLogger(State.class);

    public Consumer container;
    private List<Message> deferredQueue;
    private State overideState = null;

    public List<Message> getDeferredQueue() {
        return deferredQueue;
    }

    public Consumer getContainer() {
        return container;
    }

    public State getOverideState() {
        return overideState;
    }

    public void setOverideState(State overideState) {
        this.overideState = overideState;
    }

    protected State() {
        this.deferredQueue = new LinkedList<Message>();
    }

    public State(Consumer container) {
        setContainer(container);
        this.deferredQueue = new LinkedList<Message>();
    }

    public void setContainer(Consumer container) {
        this.container = container;
    }

    public State start() {
        // logger.debug("State " + getContainer() + " starting");
        return this;
    }

    public void send(BlockingQueue<Message> queue, Message m) {
        try {
            logger.info(getContainer() + " in state " + this + " sending " + m);
            queue.put(m);
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted while trying to send message", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }

    /**
     * Process the next message and return the next state.
     * <p>
     * The default implementation logs a warning and returns this.
     *
     * @return The next state.  Default behavior is to return this.
     */
    public State processMessage(Message m) throws MirandaException {
        State nextState = getContainer().getCurrentState();

        switch (m.getSubject()) {
            case Stop: {
                StopMessage stopMessage = (StopMessage) m;
                nextState = processStopMessage(stopMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) m;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default: {
                String message = getContainer() + " in state " + this + " does not understand " + m;
                logger.error(message);
                logger.error("Message created at", m.getWhere());
                Panic panic = new Panic(message, null, Panic.Reasons.DoesNotUnderstand);
                Miranda.getInstance().panic(panic);
            }
        }

        return nextState;
    }

    /**
     * Write any information and send a rely
     *
     * @param shutdownMessage
     * @return
     */
    public State processShutdownMessage(ShutdownMessage shutdownMessage) {
        try {
            ShutdownResponseMessage shutdownResponseMessage = new ShutdownResponseMessage(getContainer().getQueue(),
                    this, getContainer().getName());
            shutdownMessage.reply(shutdownResponseMessage);
            return StopState.getInstance();
        } catch (MirandaException e) {
            logger.error("Exception during shutdown", e);
            ShutdownPanic shutdownPanic = new ShutdownPanic(ShutdownPanic.ShutdownReasons.Exception, e);
            Miranda.panicMiranda(shutdownPanic);
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


    public boolean compare(Map<Object, Boolean> map, Object o) {
        if (map.containsKey(o)) {
            return map.get(o).booleanValue();
        }

        if (null == o || !(o instanceof State)) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        State other = (State) o;
        return getContainer().compare(map, other.getContainer());
    }

    public State processStopMessage(StopMessage stopMessage) throws MirandaException {
        getContainer().stop();

        StopState stopState = StopState.getInstance();
        return stopState;
    }

    public State ignore(Message message) {
        logger.info(this + " ignoring " + message);

        return getContainer().getCurrentState();
    }

    public State defer(Message message) {
        getDeferredQueue().add(message);

        return getContainer().getCurrentState();
    }

    public void restoreDeferredMessages() {
        for (Message message : getDeferredQueue()) {
            getContainer().getQueue().add(message);
        }

        getDeferredQueue().clear();
    }

    public State processNetworkMessage(NetworkMessage networkMessage) {
        Panic panic = new Panic("Unrecognized network message", Panic.Reasons.UnrecognizedNetworkMessage);
        Miranda.panicMiranda(panic);
        return this;
    }

    /**
     * Send a message.
     *
     * <p>
     *     If an exception occurs when trying to send the message,
     *     the method raises a panic.
     * </p>
     *
     * @param message The message to send
     * @param recipient Who to send the message to.
     */
    public void send (Message message, BlockingQueue<Message> recipient) {
        try {
            logger.info (message.getSenderObject() + " sent " + message);
            recipient.put(message);
        } catch (InterruptedException e) {
            Panic panic = new Panic(e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.panicMiranda(panic);
        }
    }

    public String toString() {
        return getClass().getSimpleName();
    }

    /**
     * Allow a state to define an optional exit action.
     *
     * <p>
     *     The base class does nothing on exit.
     * </p>
     */
    public void exit () {
    }
}
