package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Message;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectedMessage extends Message {
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }

    public ConnectedMessage(Channel channel, BlockingQueue<Message> sender, Object senderObject) {
        super(Message.Subjects.Connected, sender, senderObject);
        this.channel = channel;
    }
}
