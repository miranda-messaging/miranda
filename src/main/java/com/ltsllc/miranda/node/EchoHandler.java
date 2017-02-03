package com.ltsllc.miranda.node;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 1/22/2017.
 */
public class EchoHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private Logger logger = Logger.getLogger(EchoHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        logger.info("Client received: " + in.toString(CharsetUtil.UTF_8));
    }
}
