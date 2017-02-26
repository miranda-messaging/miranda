package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class FileChangedMessage extends Message {
    private File file;

    public File getFile() {
        return file;
    }

    public FileChangedMessage (BlockingQueue<Message> senderQueue, Object sender, File file) {
        super(Subjects.FileChanged, senderQueue, sender);

        this.file = file;
    }
}
