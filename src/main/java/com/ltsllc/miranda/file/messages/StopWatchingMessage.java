package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;

import java.io.File;
import java.util.concurrent.BlockingQueue;

public class StopWatchingMessage extends Message {
    private File file;
    private BlockingQueue<Message> listener;

    public StopWatchingMessage (BlockingQueue<Message> senderQueue, Object sender, File file, BlockingQueue<Message> listener) {
        super(Subjects.StopWatching, senderQueue, sender);

        this.file = file;
        this.listener = listener;
    }

    public File getFile() {
        return file;
    }

    public BlockingQueue<Message> getListener() {
        return listener;
    }
}
