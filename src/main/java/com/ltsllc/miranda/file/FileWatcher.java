package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class FileWatcher {
    private static Logger logger = Logger.getLogger(FileWatcher.class);

    private BlockingQueue<Message> queue;
    private Message message;

    public Message getMessage() {
        return message;
    }

    public BlockingQueue<Message> getQueue() {
        return queue;
    }

    public FileWatcher (BlockingQueue<Message> queue, Message message) {
        this.queue = queue;
        this.message = message;
    }

    public void sendMessage () {
        try {
            getQueue().put(getMessage());
        } catch (InterruptedException e) {
            Panic panic = new Panic("Exception while trying to send meaasge", e, Panic.Reasons.ExceptionSendingMessage);
            Miranda.getInstance().panic(panic);
        }
    }
}
