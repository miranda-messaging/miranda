package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.messages.Notification;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 4/2/2017.
 */
public class TestSubscriber extends TestCase {
    private Subscriber subscriber;
    private BlockingQueue<Message> queue;
    private Notification notification;

    public Notification getNotification() {
        return notification;
    }

    public BlockingQueue<Message> getQueue() {

        return queue;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void reset () {
        super.reset();

        subscriber = null;
    }

    @Before
    public void setup () {
        reset();

        super.reset();

        this.queue = new LinkedBlockingQueue<Message>();
        this.notification = new Notification(null, this);
        this.subscriber = new Subscriber(queue, notification);
    }

    @Test
    public void testConstuctor () {
        assert (getSubscriber().getNotification() != null);
        assert (getSubscriber().getQueue() != null);
    }

    @Test
    public void testNotifySubscriber () {
        getSubscriber().notifySubscriber();

        assert (contains(Message.Subjects.Notification, getQueue()));
    }

    public boolean contains (BlockingQueue<Message> queue, Object data) {
        for (Message message : queue) {
            if (message instanceof Notification) {
                Notification notification = (Notification) message;
                if (notification.getData().equals(data))
                    return true;
            }
        }

        return false;
    }

    @Test
    public void testNotifySubscriberWithData () {
        getSubscriber().notifySubscriber("whatever");

        assert (contains(getQueue(), "whatever"));
    }
}
