package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/31/2017.
 */
public class FileLoadedMessage extends Notification {
    public FileLoadedMessage (BlockingQueue<Message> senderQueue, Object sender, Object data) {
        super (Subjects.FileLoaded, senderQueue, sender, data);
    }

    public FileLoadedMessage (BlockingQueue<Message> senderQueue, Object sender) {
        super (Subjects.FileLoaded, senderQueue, sender);
    }
}
