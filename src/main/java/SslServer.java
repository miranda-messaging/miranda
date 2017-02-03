import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.security.SecureRandom;

/*
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the Apache 2.0 licence. The full text of the licence can
 * be found at https://opensource.org/licenses/Apache-2.0
 */

/**
 * Created by Clark on 1/24/2017.
 */
    public class SslServer {
    private static class EchoHandler extends ChannelInboundHandlerAdapter {

        public void channelRead (ChannelHandlerContext channelHandlerContext, Object message) {
            ByteBuf byteBuf = (ByteBuf) message;
            byte[] bytes = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, bytes);
            String s = new String(bytes);
            System.out.println ("got message " + s);
            channelHandlerContext.writeAndFlush(message);
        }
    }


    private static class LocalHandler extends ChannelInitializer<SocketChannel> {

        private SslHandler sslHandler;

        public LocalHandler(SslHandler sslHandler) {
            this.sslHandler = sslHandler;
        }

        public void initChannel(SocketChannel sc) {
            sc.pipeline().addLast(sslHandler);
            sc.pipeline().addLast(new EchoHandler());
        }
    }


    public static void main(String[] argv) {
        SslServer sslServer = new SslServer();
        sslServer.listen();
    }


    private ServerBootstrap createBootstrap () {
        ServerBootstrap serverBootstrap = null;

        try {
            SSLContext serverContext = SSLContext.getInstance("TLS");
            serverContext.init(Utils.createKeyManagers(), Utils.getTrustManagers(), new SecureRandom());
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.channel(NioServerSocketChannel.class);

            SSLEngine sslEngine = serverContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            SslHandler sslHandler = new SslHandler(sslEngine);
            LocalHandler localHandler = new LocalHandler(sslHandler);

            serverBootstrap.childHandler(localHandler);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return serverBootstrap;
    }


    private void listen() {
        try {
            int port = 6789;

            System.out.println ("Listening on port " + port);

            ServerBootstrap serverBootstrap = createBootstrap();
            serverBootstrap.bind(port).sync();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
