package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.node.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;

public class Network extends Consumer {
    private Logger logger = Logger.getLogger(Network.class);

    private static class LocalServerHandler extends ChannelInboundHandlerAdapter {
        private static Gson ourGson = new Gson();

        private BlockingQueue<Message> notify;


        public LocalServerHandler (BlockingQueue<Message> notify) {
            this.notify = notify;
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            String s = new String(buffer);
            System.out.println("got " + s);

            WireMessage wireMessage = ourGson.fromJson(s, WireMessage.class);
            NetworkMessage networkMessage = new NetworkMessage(null, wireMessage);
            Consumer.send(networkMessage, notify);
        }
    }

    private static class LocalServerInitializer extends ChannelInitializer<SocketChannel> {
        public void initChannel(SocketChannel sc) {
            LocalServerHandler localServerHandler = new LocalServerHandler(null);
            sc.pipeline().addLast(localServerHandler);
        }
    }


    private static class LocalClientHandler extends ChannelInboundHandlerAdapter {
        private static Logger logger = Logger.getLogger(LocalClientHandler.class);
        private static Gson ourGson = new Gson();

        private BlockingQueue<Message> notify;

        public LocalClientHandler (BlockingQueue<Message> notify) {
            this.notify = notify;
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            logger.info ("got " + s);

            WireMessage wireMessage = ourGson.fromJson(s, WireMessage.class);
            NetworkMessage networkMessage = new NetworkMessage(null, wireMessage);
            Consumer.send(networkMessage, notify);
        }
    }

    private static class LocalClientInializer extends ChannelInitializer<SocketChannel> {
        public void initChannel(SocketChannel sc) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) sc.remoteAddress();
            Node node = new Node(inetSocketAddress, sc);
            node.start();

            LocalClientHandler localClientHandler = new LocalClientHandler(node.getQueue());
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
                    // channelFuture.channel().pipeline().addLast(sslHandler);

                    ConnectedMessage connectedMessage = new ConnectedMessage(channelFuture.channel(), null, null);
                    Consumer.send(connectedMessage, notify);
                } else {
                    ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, channelFuture.cause(), null);
                    Consumer.send(connectFailedMessage, notify);
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

            String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE);
            String keyStorePassword = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_PASSWORD);
            String keyStoreAlias = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_ALIAS);

            String trustStoreFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String trustStorePassword = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_PASSWORD);
            String trustStoreAlias = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS);

            LocalServerInitializer localServerInitializer = new LocalServerInitializer();
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

    public void connectTo (BlockingQueue<Message> notify, String host, int port) {
        try {
            logger.info("Connecting to " + host + ":" +port);

            String trustStoreFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String trustStorePassword = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            String certificateAlias = System.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);

            LocalClientInializer localClientInializer = new LocalClientInializer();

            Bootstrap bootstrap = Utils.createClientBootstrap(localClientInializer);
            LocalClientHandler localClientHandler = new LocalClientHandler(notify);
            bootstrap.handler(localClientHandler);
            ChannelFuture channelFuture = bootstrap.connect(host, port);

            SslContext sslContext = Utils.createClientSslContext(trustStoreFilename, trustStorePassword, certificateAlias);
            LocalChannelFutureListener localChannelFutureListener = new LocalChannelFutureListener(notify, sslContext);
            channelFuture.addListener(localChannelFutureListener);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}