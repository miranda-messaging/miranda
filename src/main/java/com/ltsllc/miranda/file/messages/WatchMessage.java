package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class WatchMessage extends Message {
    private Message message;
    private File file;

    public Message getMessage() {
        return message;
    }

    public File getFile() {
        return file;
    }

    public WatchMessage (BlockingQueue<Message> senderQueue, Object sender, File file, Message message) {
        super(Subjects.Watch, senderQueue, sender);

        this.file = file;
        this.message = message;
    }
}
