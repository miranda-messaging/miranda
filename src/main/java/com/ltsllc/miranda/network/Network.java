package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.node.ConnectFailedMessage;
import com.ltsllc.miranda.node.ConnectedMessage;
import com.ltsllc.miranda.util.IOUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Network extends Consumer {
    private Logger logger = Logger.getLogger(Network.class);

    private static class LocalServerHandler extends ChannelInboundHandlerAdapter {
        private BlockingQueue<Message> notify;

        public LocalServerHandler (BlockingQueue<Message> notify) {
            this.notify = notify;
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            String s = new String(buffer);
            System.out.println("got " + s);
        }
    }

    private static class LocalServerInitializer extends ChannelInitializer<SocketChannel> {

        private SSLContext sslContext;

        public LocalServerInitializer(SSLContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel(SocketChannel sc) {
            SSLEngine sslEngine = sslContext.createSSLEngine();
            SslHandler sslHandler = new SslHandler(sslEngine);

            sc.pipeline().addLast(sslHandler);
            LocalServerHandler localServerHandler = new LocalServerHandler(null);
            sc.pipeline().addLast(localServerHandler);
        }
    }

    private static class LocalClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            System.out.println("got " + s);
        }
    }

    private static class LocalClientInializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;

        public LocalClientInializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel(SocketChannel sc) {
            SslHandler sslHandler = sslContext.newHandler(sc.alloc());
            sc.pipeline().addLast(sslHandler);

            LocalClientHandler localClientHandler = new LocalClientHandler();
            sc.pipeline().addLast(localClientHandler);
        }

    }

    private static class LocalChannelFutureListener implements ChannelFutureListener {
        private BlockingQueue<Message> notify;
        private SslContext sslContext;


        public LocalChannelFutureListener (BlockingQueue<Message> notify, SslContext sslContext) {
            this.notify = notify;
            this.sslContext = sslContext;
        }

        public void operationComplete (ChannelFuture channelFuture) {
            try {
                if (channelFuture.isSuccess()) {
                    SslHandler sslHandler = sslContext.newHandler(channelFuture.channel().alloc());

                    channelFuture.channel().pipeline().addLast(sslHandler);
                    ConnectedMessage connectedMessage = new ConnectedMessage(channelFuture.channel(), null);
                    notify.put(connectedMessage);
                } else {
                    ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, channelFuture.cause());
                    notify.put(connectFailedMessage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public Network (BlockingQueue<Message> queue) {
        super("Network");

        setQueue(queue);
        setCurrentState(new ReadyState((this)));
    }


    private SSLContext createServerContext()
    {
        SSLContext sslContext = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(Utils.createKeyManagers(), Utils.getTrustManagers(), new SecureRandom());

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }

    private ServerBootstrap createServerBootstrap (BlockingQueue<Message> notify) {
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

            SSLContext sslContext = createServerContext();
            LocalServerInitializer localServerInitializer = new LocalServerInitializer(sslContext);
            serverBootstrap.childHandler(localServerInitializer);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return serverBootstrap;
    }

    public void listen (int port) {
        ServerBootstrap serverBootstrap = createServerBootstrap(null);
        serverBootstrap.bind(port);
    }

    private SslContext createClientContext () {
        SslContext sslContext = null;

        try {
            SSLContext context = SSLContext.getDefault();
            SSLSocketFactory sf = context.getSocketFactory();
            String[] cipherSuites = sf.getSupportedCipherSuites();
            List<String> ciphers = Arrays.asList(cipherSuites);

            TrustManagerFactory trustManagerFactory = createTrustManagerFactory();

            sslContext = SslContextBuilder
                    .forClient()
                    .ciphers(ciphers)
                    .trustManager(trustManagerFactory)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }

    private TrustManagerFactory createTrustManagerFactory() {
        FileInputStream fileInputStream = null;
        TrustManagerFactory trustManagerFactory = null;

        try {
            fileInputStream = new FileInputStream(System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE));
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            char[] password = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD).toCharArray();

            keyStore.load(fileInputStream, password);

            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fileInputStream);
        }

        return trustManagerFactory;
    }

    private Bootstrap createClientBootstrap () {
        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        LocalClientHandler localClientHandler = new LocalClientHandler();
        bootstrap.handler(localClientHandler);

        return bootstrap;
    }

    public void connectTo (BlockingQueue<Message> notify, String host, int port) {
        try {
            Bootstrap bootstrap = createClientBootstrap();
            LocalClientHandler localClientHandler = new LocalClientHandler();
            bootstrap.handler(localClientHandler);
            ChannelFuture channelFuture = bootstrap.connect(host, port);
            SslContext sslContext = createClientContext();

            LocalChannelFutureListener localChannelFutureListener = new LocalChannelFutureListener(notify, sslContext);
            channelFuture.addListener(localChannelFutureListener);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}