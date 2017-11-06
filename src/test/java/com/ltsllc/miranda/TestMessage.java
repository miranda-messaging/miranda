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
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * Created by Clark on 3/27/2017.
 */
public class TestMessage extends TestCase {
    public static class TestBlockingQueue extends LinkedBlockingQueue<Message> {
        private InterruptedException putException;

        public void setPutException(InterruptedException putException) {
            this.putException = putException;
        }

        public void put(Message message) throws InterruptedException {
            if (null != putException)
                throw putException;

            super.put(message);
        }
    }

    private Message message;
    private BlockingQueue<Message> queue;

    public Message getMessage() {
        return message;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public void reset () throws MirandaException {
        super.reset();

        message = null;
    }

    @Before
    public void setup () throws MirandaException {
        reset();

        super.setup();

        queue = new LinkedBlockingQueue<Message>();
        message = new Message(Message.Subjects.Version, queue, this);
    }

    @Test
    public void testConstructor () {
        assert (getMessage().getSender() == getQueue());
        assert (getMessage().getSenderObject() == this);
        assert (getMessage().getSubject().equals(Message.Subjects.Version));
        assert (getMessage().getWhere() != null);
    }

    @Test
    public void testReplySuccess () throws MirandaException {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getMessage().setSender(queue);

        Message reply = new Message (Message.Subjects.Version, null, null);

        getMessage().reply(reply);

        assert (contains(Message.Subjects.Version, queue));
    }

    @Test
    public void testReplyException () throws MirandaException {
        setupMockMiranda();
        TestBlockingQueue testBlockingQueue = new TestBlockingQueue();
        InterruptedException interruptedException = new InterruptedException("test");
        testBlockingQueue.setPutException(interruptedException);

        getMessage().setSender(testBlockingQueue);

        Message reply = new Message (Message.Subjects.Version, null, null);

        getMessage().reply(reply);

        verify(getMockMiranda(), atLeastOnce()).panic(Matchers.any(Panic.class));
    }

    @Test
    public void testEquals () {
        assert (getMessage().equals(getMessage()));
        assert (!getMessage().equals(null));
    }
}
