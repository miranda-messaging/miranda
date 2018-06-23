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
import com.ltsllc.miranda.cluster.states.ClusterReadyState;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/28/2017.
 */
public class TestState extends TestCase {
    public static class TestBlockingQueue extends LinkedBlockingQueue<Message> {
        private InterruptedException interruptedException;

        public void setInterruptedException(InterruptedException interruptedException) {
            this.interruptedException = interruptedException;
        }

        public void put (Message message) throws InterruptedException {
            if (null != interruptedException)
                throw interruptedException;

            super.put(message);
        }
    }
    @Mock
    private Consumer mockConsumer;

    private BlockingQueue<Message> queue;
    private State state;

    public State getState() {
        return state;
    }

    public Consumer getMockConsumer() {
        return mockConsumer;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void reset () throws Exception {
        super.reset();

        mockConsumer = null;
        queue = null;
        state = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        this.state = new ClusterReadyState(getMockCluster());
    }

    @Test
    public void testProcessStopMessage () throws MirandaException {
        StopMessage stopMessage = new StopMessage(null, this);

        State nextState = getState().processMessage(stopMessage);

        assert (nextState instanceof StopState);
    }

    @Test
    public void testEquals () {
        assert (getState().equals(getState()));
        assert (!getState().equals(null));
    }

    @Test
    public void testStart () {
        setuplog4j();

        State nextState = getState().start();

        assert (nextState == getState());
    }

}
