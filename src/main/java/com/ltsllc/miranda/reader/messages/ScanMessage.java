package com.ltsllc.miranda.reader.messages;

import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * A {@link Message} that asks the receiver to scan a directory.
 *
 * <p>
 *     The proper
 * </p>
 */
public class ScanMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ScanMessage (String filename, BlockingQueue<Message> senderQueue, Object senderObject) {
        super(Subjects.Scan, senderQueue, senderObject);
        setFilename(filename);
    }

}
