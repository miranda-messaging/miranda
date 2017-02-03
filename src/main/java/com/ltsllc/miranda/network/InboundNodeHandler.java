package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.WireMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/30/2017.
 */
public class InboundNodeHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = Logger.getLogger(InboundNodeHandler.class);

    private static Gson ourGson = new Gson();

    private BlockingQueue<Message> network;
    private BlockingQueue<Message> node;

    public InboundNodeHandler (BlockingQueue<Message> network, BlockingQueue<Message> node) {
        this.network = network;
        this.node = node;
    }

    public BlockingQueue<Message> getNetwork() {
        return network;
    }

    public BlockingQueue<Message> getNode() {
        return node;
    }

    public void channelRead (ChannelHandlerContext channelHandlerContext, Object o) {
        ByteBuf byteBuf = (ByteBuf) o;

        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, buffer);
        String s = new String(buffer);

        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        InputStreamReader isr = new InputStreamReader(bais);
        WireMessage wireMessage = ourGson.fromJson(isr, WireMessage.class);

        NetworkMessage networkMessage = new NetworkMessage(getNetwork(), wireMessage);

        try {
            getNode().put(networkMessage);
        } catch (InterruptedException e) {
            logger.error("Interrupted while trying to send message", e);
        }
    }
}
