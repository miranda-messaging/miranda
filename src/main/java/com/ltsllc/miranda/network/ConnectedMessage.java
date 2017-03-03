package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectedMessage extends Message {
    private int handle;

    public int getHandle() {
        return handle;
    }

    public ConnectedMessage(BlockingQueue<Message> sender, Object senderObject, int handle) {
        super(Message.Subjects.Connected, sender, senderObject);

        this.handle = handle;
    }
}
