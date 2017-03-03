package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class SendMessageMessage extends Message {
    private int handle;
    private byte[] content;

    public int getHandle () {
        return handle;
    }

    public byte[] getContent() {
        return content;
    }

    public SendMessageMessage (BlockingQueue<Message> queue, Object sender, int handle, byte[] content) {
        super(Subjects.SendMessage, queue, sender);

        this.handle = handle;
        this.content = content;
    }


    public SendMessageMessage (BlockingQueue<Message> queue, Object sender, int handle, String content) {
        super(Subjects.SendMessage, queue, sender);

        this.handle = handle;
        this.content = content.getBytes();
    }
}
