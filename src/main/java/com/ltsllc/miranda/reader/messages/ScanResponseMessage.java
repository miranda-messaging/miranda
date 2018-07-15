package com.ltsllc.miranda.reader.messages;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.message.Message;

import java.util.concurrent.BlockingQueue;

/**
 * The results of scanning a directory
 *
 * <p>
 *     The contents are an absolute path to the entry.
 * </p>
 *
 * <
 */
public class ScanResponseMessage extends Message {
    private String filename;
    private String[] contents;
    private Results result;

    public Results getResult() {
        return result;
    }

    public void setResult(Results result) {
        this.result = result;
    }

    public String[] getContents() {
        return contents;
    }

    public void setContents(String[] contents) {
        this.contents = contents;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ScanResponseMessage (Results result, String filename, String[] contents, BlockingQueue<Message> senderQueue,
                                Object senderObject)
    {
        super(Subjects.ScanResponseMessage, senderQueue, senderObject);
        setFilename(filename);
        setContents(contents);
        setResult(result);
    }
}
