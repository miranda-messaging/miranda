package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.BlockingQueue;

/**
 * A class that sits in its own thread and waits for messages from the
 * network.  When messages arrive it sends a {@link } to the associated
 * network object.
 */
public class NetworkSelector implements Runnable {
    private Logger logger = Logger.getLogger(NetworkSelector.class);

    private BlockingQueue<Message> notify;
    private Message message;
    private Selector selector;
    private boolean terminate = false;
    private Thread thread;

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public BlockingQueue<Message> getNotify() {
        return notify;
    }

    public Message getMessage() {
        return message;
    }

    public boolean terminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public Selector getSelector() {

        return selector;
    }

    public NetworkSelector(BlockingQueue<Message> notify, Message message) throws Panic {
        this.notify = notify;
        this.message = message;
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            throw new StartupPanic("Exception during selector creation", e, StartupPanic.StartupReasons.CreatingSelector);
        }
    }

    /**
     * Create and start a new instance.
     *
     * @param notify  Who should the new instance notify when select indicates
     *                a socket is ready.
     * @param message The message that should be sent when a socket is ready.
     * @return The new instance.
     * @throws Panic If there is a problem creating the instance
     */
    public static NetworkSelector start(BlockingQueue<Message> notify, Message message) throws Panic {
        NetworkSelector networkSelector = new NetworkSelector(notify, message);
        Thread thread = new Thread(networkSelector);
        networkSelector.setThread(thread);
        thread.start();

        return networkSelector;
    }

    public void run() {
        while (!terminate()) {
            select();
            try {
                getNotify().put(message);
            } catch (InterruptedException e) {
                logger.error("Exception during message send", e);
                Panic panic = new Panic("Exception during message send", e, Panic.Reasons.ExceptionSendingMessage);
                if (Miranda.getInstance().panic(panic))
                    setTerminate(true);
            }
        }
    }


    public void select() {
        try {
            int numberReady = selector.select();
        } catch (IOException e) {
            Panic panic = new Panic("Exception during select", e, Panic.Reasons.Select);
            if (Miranda.getInstance().panic(panic)) {
                setTerminate(true);
            }
        }
    }


    public void add(SelectableChannel selectableChannel) throws ClosedChannelException {
        int operation = SelectionKey.OP_READ;
        selectableChannel.register(selector, operation);
    }

    /**
     * Calling this method will call {@link SelectableChannel#close()} on the argument,
     * removing it from future selects.
     *
     * @param selectableChannel The channel to be closed.
     * @throws IOException If there is a problem closing the argument.
     */
    public void remove(SelectableChannel selectableChannel) throws IOException {
        selectableChannel.close();
    }
}
