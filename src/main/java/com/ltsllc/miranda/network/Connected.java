package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 1/21/2017.
 */
public class Connected extends Message {
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public Connected (Channel channel, BlockingQueue<Message> sender) {
        super(Message.Subjects.Connected, sender);
        this.channel = channel;
    }
}
