package com.ltsllc.miranda.node;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

/**
 * Created by Clark on 1/29/2017.
 */
public class NodeHandler extends ChannelInboundHandlerAdapter {
    private static Gson ourGson;

    private Node node;

    public Node getNode() {
        return node;
    }

    public NodeHandler (Node node) {
        this.node = node;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, buffer);
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        InputStreamReader inputStreamReader = new InputStreamReader(bais);
        WireMessage wireMessage = ourGson.fromJson(inputStreamReader, WireMessage.class);
        NetworkMessage networkMessage = new NetworkMessage(null, wireMessage);
        getNode().getQueue().put(networkMessage);
    }
}
