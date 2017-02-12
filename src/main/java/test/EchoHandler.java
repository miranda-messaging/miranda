package test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 2/4/2017.
 */
public class EchoHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = Logger.getLogger(EchoHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,buffer);
        String s = new String(buffer);

        System.out.println ("got " + s);

        ctx.writeAndFlush(byteBuf);
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn ("channelInactive, closing channel");

        ctx.close();
    }
}
