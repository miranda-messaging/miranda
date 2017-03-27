package com.ltsllc.miranda;

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

    public void reset () {
        super.reset();

        message = null;
    }

    @Before
    public void setup () {
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
    public void testReplySuccess () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        getMessage().setSender(queue);

        Message reply = new Message (Message.Subjects.Version, null, null);

        getMessage().reply(reply);

        assert (contains(Message.Subjects.Version, queue));
    }

    @Test
    public void testReplyException () {
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
