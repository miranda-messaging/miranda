package com.ltsllc.miranda.writer;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/29/2017.
 */
public class WriteFailedMessage extends Message {
    private String filename;
    private Throwable cause;

    public String getFilename() {
        return filename;
    }

    public Throwable getCause() {
        return cause;
    }

    public WriteFailedMessage (BlockingQueue<Message> sender, String filename, Throwable cause) {
        super(Subjects.WriteFailed, sender);

        this.filename = filename;
        this.cause = cause;
    }
}
