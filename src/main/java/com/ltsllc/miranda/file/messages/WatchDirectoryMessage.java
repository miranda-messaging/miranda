package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.message.Message;

import java.io.File;
import java.util.concurrent.BlockingQueue;

import static com.ltsllc.miranda.message.Message.Subjects.WatchDirectory;

public class WatchDirectoryMessage extends Message {
    private File directory;
    private BlockingQueue<Message> listener;

    public WatchDirectoryMessage(BlockingQueue<Message> senderQueue, Object sender, File directory, BlockingQueue<Message> listener) {
        super(WatchDirectory, senderQueue, sender);

        this.directory = directory;
        this.listener = listener;
    }

    public File getDirectory() {
        return directory;
    }

    public BlockingQueue<Message> getListener() {
        return listener;
    }
}
