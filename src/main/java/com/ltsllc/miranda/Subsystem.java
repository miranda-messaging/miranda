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

import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A Miranda subsystem.
 *  <p>
 *      A key aspect of being a subsystem is that it runs in it's own thread.
 *  </p>
 * <H2>Attributes</H2>
 * <UL>
 * <LI>name - the name of the subsystem</LI>
 * <LI>queue - the queue that the subsystem gets messages from.</LI>
 * <LI>thre - the thread the subystem is running in.</LI>
 * </UL>
 * <p>
 * <H2>Methods</H2>
 * <UL>
 * <LI></LI>
 * </UL>
 * Created by Clark on 12/30/2016.
 */
public abstract class Subsystem implements Runnable, Comparer {
    static private Logger logger = Logger.getLogger("Subsytem");

    private String name;
    private transient BlockingQueue<Message> queue;
    private transient Thread thread;
    private transient boolean started;
    private transient boolean stopped;

    public boolean getStopped() {
        return stopped;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void setQueue(BlockingQueue<Message> newQueue) {
        queue = newQueue;
    }

    public Thread getThread() {
        return thread;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Subsystem() {
    }

    public Subsystem(String name) {
        basicConstructor(name, new LinkedBlockingQueue<Message>());
    }

    public Subsystem(String name, BlockingQueue<Message> queue) {
        basicConstructor(name, queue);
    }

    public void basicConstructor(String name, BlockingQueue<Message> queue) {
        if (null == queue)
            queue = new LinkedBlockingQueue<Message>();

        this.started = false;
        this.stopped = false;
        this.name = name;
        this.queue = queue;
    }

    /**
     * Start the subsystem.
     * <p>
     * <p>
     * This version of the method starts a new thread.
     * </P>
     */
    public void start() {
        thread = new Thread(this, getName());
        thread.start();
        setStarted(true);
    }

    public void stop() {
        this.stopped = true;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof Subsystem))
            return false;

        Subsystem other = (Subsystem) o;

        if (!getName().equals(other.getName()))
            return false;


        if (!getQueue().equals(other.getQueue()))
            return false;

        return getThread().equals(other.getThread());
    }

    public Message getNextMessage () {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception getting the next message",e);
            Miranda.panicMiranda(panic);
            return null;
        }
    }


}
