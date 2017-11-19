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

package com.ltsllc.miranda.test;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.ConnectionListener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class for use in testing {@link ConnectionListener}
 */
public class TestNetworkListener extends ConnectionListener {
    public enum EventTypes {
        NewHandle,
        Exception,
        Stop
    }

    public static class Event {
        private EventTypes type;

        public EventTypes getType() {
            return type;
        }

        public Event(EventTypes type) {
            this.type = type;
        }
    }

    public static class NewHandleEvent extends Event {
        private Handle handle;

        public Handle getHandle() {
            return handle;
        }

        public NewHandleEvent(Handle handle) {
            super(EventTypes.NewHandle);

            this.handle = handle;
        }
    }

    public static class StopEvent extends Event {
        public StopEvent() {
            super(EventTypes.Stop);
        }
    }

    public static class ExceptionEvent extends Event {
        private Throwable throwable;

        public Throwable getThrowable() {
            return throwable;
        }

        public ExceptionEvent(Throwable throwable) {
            super(EventTypes.Exception);

            this.throwable = throwable;
        }
    }

    public static class TestConnector implements Runnable {
        private BlockingQueue<Event> eventsQueue;
        private BlockingQueue<Handle> handleQueue;
        private boolean keepGoing;

        public boolean keepGoing() {
            return keepGoing;
        }

        public BlockingQueue<Event> getEventsQueue() {
            return eventsQueue;
        }

        public BlockingQueue<Handle> getHandleQueue() {
            return handleQueue;
        }

        public TestConnector(BlockingQueue<Event> eventsQueue, BlockingQueue<Handle> handleQueue) {
            this.eventsQueue = eventsQueue;
            this.handleQueue = handleQueue;
        }

        public void run() {
            while (keepGoing()) {
                Event event = null;

                try {
                    event = getEventsQueue().take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }

                switch (event.getType()) {
                    case Exception: {
                        ExceptionEvent exceptionEvent = (ExceptionEvent) event;
                        break;
                    }

                    case NewHandle: {
                        NewHandleEvent newHandleEvent = (NewHandleEvent) event;
                        break;
                    }

                    case Stop: {
                        keepGoing = false;
                        break;
                    }
                }
            }
        }
    }

    private NetworkException networkException;
    private Throwable startupCalltrace;
    private BlockingQueue<Event> eventsQueue;

    public NetworkException getNetworkException() {
        return networkException;
    }

    public void setNetworkException(NetworkException networkException) {
        this.networkException = networkException;
    }

    public Throwable getStartupCalltrace() {
        return startupCalltrace;
    }

    public void setStartupCalltrace(Throwable throwable) {
        startupCalltrace = throwable;
    }

    public boolean startupCalled() {
        return null != getStartupCalltrace();
    }

    public TestNetworkListener(int port) throws MirandaException {
        super(port);
    }

    public BlockingQueue<Event> getEventsQueue() {
        return eventsQueue;
    }

    public void setEventsQueue(BlockingQueue<Event> eventsQueue) {
        this.eventsQueue = eventsQueue;
    }

    public void startup(BlockingQueue<Handle> queue) throws NetworkException {
        LinkedBlockingQueue<Event> eventsQueue = new LinkedBlockingQueue<Event>();
        setEventsQueue(eventsQueue);

        TestConnector testConnector = new TestConnector(getEventsQueue(), queue);
        Thread thread = new Thread(testConnector);
        thread.start();

        Exception e = new Exception();
        e.fillInStackTrace();
        setStartupCalltrace(e);

        if (null == getNetworkException())
            return;
        else {
            throw getNetworkException();
        }
    }

    public void putEvent(Event event) {
        try {
            getEventsQueue().put(event);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void putHandle(Handle handle) {
        try {
            getHandleQueue().put(handle);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stopListening() {
        // TODO: implement this
    }
}
