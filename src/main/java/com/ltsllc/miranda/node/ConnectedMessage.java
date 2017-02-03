package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 12/31/2016.
 */
public class ConnectedMessage extends Message {
    private Channel channel;

    public ConnectedMessage(Channel channel, BlockingQueue<Message> sender)
    {
        super(Subjects.Connected, sender);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }
}
