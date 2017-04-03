package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.file.messages.Notification;
import com.ltsllc.miranda.miranda.Miranda;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class Subscriber {
    private BlockingQueue<Message> queue;
    private Notification notification;

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public Notification getNotification() {
        return notification;
    }

    public Subscriber (BlockingQueue<Message> queue, Notification notification) {
        this.queue = queue;
        this.notification = notification;
    }

    public void notifySubscriber () {
        getNotification().setData(null);

        try {
            getQueue().put(getNotification());
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted while trying to notify suscriber", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }

    public void notifySubscriber (Object data) {
        getNotification().setData(data);

        try {
            getQueue().put(getNotification());
        } catch (InterruptedException e) {
            Panic panic = new Panic("Interrupted while trying to notify suscriber", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }
}
