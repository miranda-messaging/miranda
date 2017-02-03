package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.WireMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 1/21/2017.
 */
public class NodeHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = Logger.getLogger(NodeHandler.class);
    private static Gson ourGson = new Gson();

    private BlockingQueue<Message> node;

    public NodeHandler (Node node) {
        this.node = node.getQueue();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0, buffer);
        String s = new String(buffer);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);

        WireMessage wireMessage = ourGson.fromJson(inputStreamReader, WireMessage.class);
        NetworkMessage networkMessage = new NetworkMessage(null, wireMessage);

        try {
            node.put(networkMessage);
        } catch (InterruptedException e) {
            logger.error("Interrupted while sending a message", e);
        }
    }


}
