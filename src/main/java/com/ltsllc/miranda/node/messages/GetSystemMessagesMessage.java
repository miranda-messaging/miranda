package com.ltsllc.miranda.node.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/22/2017.
 */
public class GetSystemMessagesMessage extends Message {
    private String filename;

    public String getFilename() {
        return filename;
    }

    public GetSystemMessagesMessage (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        super(Subjects.GetSystemMessages, senderQueue, sender);

        this.filename = filename;
    }
}
