package com.ltsllc.miranda.cluster;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/20/2017.
 */
public class NodeHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = Logger.getLogger(NodeHandler.class);

    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        logger.info ("got message " + msg);
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

}
