package com.ltsllc.miranda.network.messages;

import com.ltsllc.miranda.Message;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class DisconnectedMessage extends Message {
    private int handle;

    public int getHandle() {
        return handle;
    }

    public DisconnectedMessage(BlockingQueue<Message> senderQueue, Object sender, int handle) {
        super (Subjects.Disconnected, senderQueue, sender);

        this.handle = handle;
    }
}
