package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.util.Utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 5/14/2017.
 */
public class ReadResponseMessage extends Message {
    private String additionalInfo;
    private String filename;
    private Results result;
    private byte[] data;

    public String getFilename() {
        return filename;
    }

    public Results getResult() {
        return result;
    }

    public byte[] getData() {
        return data;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public ReadResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Results result, byte[] data) {
        super(Subjects.ReadResponse, senderQueue, sender);

        this.result = result;
        this.data = data;
    }

    public ReadResponseMessage (BlockingQueue<Message> senderQueue, Object sender, Throwable throwable) {
        super(Subjects.ReadResponse, senderQueue, sender);

        StringWriter stringWriter = null;

        try {
            stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            this.additionalInfo = stringWriter.toString();
        } finally {
            Utils.closeIgnoreExceptions(stringWriter);
        }
    }
}
