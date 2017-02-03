package com.ltsllc.miranda.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Created by Clark on 2/1/2017.
 */
public class SslClient {
    private ChannelHandlerContext channelHandlerContext;

    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] array = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, array);
            String s = new String(array);
            System.out.println("got " + s);
            System.exit(0);
        }
    }

    public static class LocalInitializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;

        public LocalInitializer(SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel(SocketChannel sc) {
            SslHandler sslHandler = sslContext.newHandler(sc.alloc());
            sc.pipeline().addLast(sslHandler);
            LocalHandler localHandler = new LocalHandler();
            sc.pipeline().addLast(localHandler);
        }
    }



    public static void main(String[] argv) {
        DOMConfigurator.configure("log4j.xml");
        SslClient sslClient = new SslClient();
        sslClient.doit();
    }

    private Bootstrap createClientBootstrap () {
        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup);

        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

        return bootstrap;
    }

    private static class LocalChannelFutureListener implements ChannelFutureListener {
        private SslContext sslContext;

        public LocalChannelFutureListener (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if (channelFuture.isSuccess())
            {
                SslHandler handler = sslContext.newHandler(channelFuture.channel().alloc());
                channelFuture.channel().pipeline().addLast(handler);

                ByteBuf byteBuf = Unpooled.directBuffer(256);
                ByteBufUtil.writeUtf8(byteBuf, "Hello world!");
                channelFuture.channel().writeAndFlush(byteBuf).sync();
            }
            else
            {
                channelFuture.cause().printStackTrace();
                System.exit(1);
            }
        }
    }

    public void doit() {
        try {
            Bootstrap bootstrap = createClientBootstrap();
            SslContext serverContext = Utils.createSslContext();
            LocalInitializer localHandler = new LocalInitializer(serverContext);
            bootstrap.handler(localHandler);

            String host = "localhost";
            int port = 6789;

            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            LocalChannelFutureListener localChannelFutureListener = new LocalChannelFutureListener(serverContext);
            channelFuture.addListener(localChannelFutureListener);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
