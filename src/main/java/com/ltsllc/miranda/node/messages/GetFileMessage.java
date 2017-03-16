package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileMessage extends Message {
    private String filename;

    public GetFileMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        super(Subjects.GetFile, senderQueue, sender);

        this.filename = filename;
    }

    public String getFilename () {
        return filename;
    }
}
