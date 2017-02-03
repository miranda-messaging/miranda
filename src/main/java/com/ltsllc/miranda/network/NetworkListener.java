package com.ltsllc.miranda.network;

import com.ltsllc.miranda.file.MirandaProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;


/**
 * Created by Clark on 1/31/2017.
 */
public class NetworkListener {
    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = Logger.getLogger(LocalHandler.class);

        public void channelRead (ChannelHandlerContext channelHandlerContext, Object message) {
            ByteBuf byteBuf = (ByteBuf) message;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            logger.info ("Got " + s);
            channelHandlerContext.writeAndFlush(byteBuf);
        }
    }


    private static class LocalInitializer extends ChannelInitializer<SocketChannel> {
        private static Logger logger = Logger.getLogger(LocalInitializer.class);

        private SslContext sslContext;

        public LocalInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel (SocketChannel socketChannel) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.remoteAddress();
            logger.info("Got connection from " + inetSocketAddress);

            SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
            socketChannel.pipeline().addLast(sslHandler);
            socketChannel.pipeline().addLast(new LocalHandler());
        }
    }

    private static class LocalChannelListener implements ChannelFutureListener {
        public void operationComplete (ChannelFuture channelFuture) {
            if (channelFuture.isSuccess())
            {
                System.out.println ("Got connection");
            }
            else
            {
                System.out.println ("Connect faild");
            }
        }

    }

    private Logger logger = Logger.getLogger(NetworkListener.class);

    private int port;

    public NetworkListener (int port) {
        this.port = port;
    }

    /*
    private ServerBootstrap createServerBootstrap () {
        ServerBootstrap serverBootstrap = null;

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.channel(NioServerSocketChannel.class);

            SslContext sslContext = createServerContext();
            LocalInitializer localInitializer = new LocalInitializer(sslContext);
            serverBootstrap.childHandler(localInitializer);

        return serverBootstrap;
    }
    */




    public int getPort() {
        return port;
    }


    public void listen() {
        String certificateFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String certificatePassword = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
        String certificateAlias = System.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);

        SslContext sslContext = Utils.createServerSslContext(certificateFilename, certificatePassword, certificateAlias);
        LocalInitializer localInitializer = new LocalInitializer(sslContext);

        ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localInitializer);

        logger.info ("listening at " + port);
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channelFuture.addListener(new LocalChannelListener());
    }
}
