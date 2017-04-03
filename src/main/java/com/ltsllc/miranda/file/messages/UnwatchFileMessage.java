package com.ltsllc.miranda.file.messages;

import com.ltsllc.miranda.Message;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/25/2017.
 */
public class UnwatchFileMessage extends Message {
    private File file;

    public File getFile() {
        return file;
    }


    public UnwatchFileMessage (BlockingQueue<Message> senderQueue, Object sender, File file) {
        super(Subjects.UnwatchFile, senderQueue, sender);

        this.file = file;
    }
}
