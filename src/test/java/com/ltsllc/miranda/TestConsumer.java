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

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestConsumer extends TestCase {
    public static class TestState extends State {
        private Error startException;
        private Error processMessageException;

        public void setProcessMessageException(Error processMessageException) {
            this.processMessageException = processMessageException;
        }

        public void setStartException (Error error) {
            this.startException = error;
        }

        public TestState (Consumer consumer) {
            super(consumer);
        }

        public State start () {
            if (null != startException)
                throw startException;

            return this;
        }

        public State processMessage (Message message) {
            if (null != processMessageException)
                throw processMessageException;

            return this;
        }
    }

    @Mock
    private State mockState;

    private Consumer consumer;
    private TestState testState;

    public Consumer getConsumer() {
        return consumer;
    }

    public TestState getTestState() {
        return testState;
    }

    public State getMockState() {
        return mockState;
    }

    public void reset () {
        super.reset();

        mockState = null;
        consumer = null;
        testState = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockState = mock (State.class);
        consumer = new Consumer("whatever");
        testState = new TestState(consumer);
        consumer.setCurrentState(testState);
    }

    @Test
    public void testRunSuccess () {
        getConsumer().start();
        assert (getConsumer().getCurrentState() instanceof TestState);
    }

    @Test
    public void testStartCurrentStateSuccess () {
        getConsumer().start();
        assert (getConsumer().getCurrentState() instanceof TestState);
    }

    @Test
    public void testStartCurrentStateNullCurrentState () {
        setupMockMiranda();
        getConsumer().setCurrentState(null);

        getConsumer().startCurrentState();

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    @Test
    public void testStartCurrentStateException () {
        setupMockMiranda();
        Error error = new Error ("test");
        getTestState().setStartException(error);

        getConsumer().startCurrentState();

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    public void putMessage (BlockingQueue<Message> queue, Message message) {
        try {
            queue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetNextMessageSuccess () {
        Message message = new Message(Message.Subjects.Version, null, null);
        putMessage(getConsumer().getQueue(), message);

        message = getConsumer().getNextMessage();

        assert (message != null);
    }

    @Test
    public void testProcessMessageSuccess () {
        setupMockMiranda();
        getConsumer().setCurrentState(getMockState());

        Message message = new Message(Message.Subjects.Version, null, null);

        State nextState = getConsumer().processMessage(message);

        verify(getMockState(), atLeastOnce()).processMessage(Matchers.eq(message));
    }

    @Test
    public void testProcessMessageNullCurrentState () {
        setupMockMiranda();

        getConsumer().setCurrentState(null);

        Message message = new Message(Message.Subjects.Version, null, null);

        getConsumer().processMessage(message);

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    @Test
    public void testProcessMessageException () {
        setupMockMiranda();

        Error error = new Error("test");
        getTestState().setProcessMessageException(error);

        Message message = new Message(Message.Subjects.Version, null, null);

        getConsumer().processMessage(message);

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    @Test
    public void testCompare () {
        Map<Object, Boolean> map = new HashMap<Object, Boolean>();

        assert (getConsumer().compare(map, getConsumer()));
        assert (!getConsumer().compare(map, null));
    }
}
