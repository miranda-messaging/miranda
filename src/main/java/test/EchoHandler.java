package test;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Clark on 2/4/2017.
 */
public class EchoHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] buffer = new byte[byteBuf.readableBytes()];
        byteBuf.getBytes(0,buffer);
        String s = new String(buffer);

        System.out.println ("got " + s);

        ctx.writeAndFlush(byteBuf);
    }
}
