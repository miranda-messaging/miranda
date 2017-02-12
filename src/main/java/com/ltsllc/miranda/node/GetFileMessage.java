package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileMessage extends Message {
    private String file;

    public GetFileMessage (BlockingQueue<Message> senderQueue, Object sender, String file) {
        super(Subjects.GetFile, senderQueue, sender);

        this.file = file;
    }

    public String getFile() {
        return file;
    }
}
