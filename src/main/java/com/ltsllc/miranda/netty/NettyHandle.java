package com.ltsllc.miranda.netty;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.CloseMessage;
import com.ltsllc.miranda.network.ClosedMessage;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.SendMessageMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;

import java.util.concurrent.BlockingQueue;

/**
 * A handle for use with the netty library
 */
public class NettyHandle extends Handle {
    private Channel channel;


    public NettyHandle(int handle, BlockingQueue<Message> notify, Channel channel) {
        super(handle, notify);

        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public void send (SendMessageMessage sendMessageMessage) {
        ByteBuf byteBuf = Unpooled.directBuffer(sendMessageMessage.getContent().length);
        byteBuf.setBytes(0, sendMessageMessage.getContent());
        getChannel().writeAndFlush(byteBuf);
    }


    public void close (CloseMessage disconnectMessage) {
        getChannel().close();

        ClosedMessage closedMessage = new ClosedMessage(null, this, getHandle());
        disconnectMessage.reply(closedMessage);
    }


    public void panic () {
        getChannel().close();
    }

}
