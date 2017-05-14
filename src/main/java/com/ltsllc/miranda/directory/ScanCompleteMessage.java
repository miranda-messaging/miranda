package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/13/2017.
 */
public class ScanCompleteMessage extends Message {
    private List<File> files;

    public List<File> getFiles() {
        return files;
    }

    public ScanCompleteMessage (BlockingQueue<Message> senderQueue, Object sender, List<File> files) {
        super(Subjects.ScanCompleteMessage, senderQueue, sender);

        this.files = files;
    }
}
