package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.node.NetworkMessage;

/*
import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.node.ConnectedMessage;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.WireMessage;
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

import javax.net.ssl.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/1/2017.
 */
public class Network_backup extends Consumer {
    public Network_backup()
    {
        super("network_backup");
    }
    /*
        private static Logger logger = Logger.getLogger(com.ltsllc.miranda.network.Network.class);

        private Bootstrap bootstrap;

        private static class ReceiverHandler extends ChannelInboundHandlerAdapter {

            private Gson ourGson = new Gson();

            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] buffer = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(0, buffer);
                String s = new String(buffer);

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);

                WireMessage wireMessage = ourGson.fromJson(inputStreamReader, WireMessage.class);
            }
        }

        private static class ClientHandler extends ChannelInboundHandlerAdapter {
            private BlockingQueue<Message> notify;

            public ClientHandler(BlockingQueue<Message> notify) {
                this.notify = notify;
            }

            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                NetworkMessage networkMessage = new NetworkMessage(null, null);
                try {
                    notify.put(networkMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }


        private static class LocalClientChannelInitializer extends ChannelInitializer<SocketChannel> {
            private SslContext sslContext;
            private BlockingQueue<Message> notify;

            public LocalClientChannelInitializer (SslContext sslContext, BlockingQueue<Message> notify) {
                this.notify = notify;
                this.sslContext = sslContext;
            }

            public void initChannel(SocketChannel sc) {
                SslHandler sslHandler = sslContext.newHandler(sc.alloc());
                sc.pipeline().addLast(sslHandler);

                com.ltsllc.miranda.network.Network.ClientHandler clientHandler = new com.ltsllc.miranda.network.Network.ClientHandler(notify);
                sc.pipeline().addLast(clientHandler);

                ConnectedMessage connectedMessage = new ConnectedMessage(sc.pipeline().channel(), null);

                try {
                    notify.put(connectedMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        }
        private static class LocalServerInitializer extends ChannelInitializer<SocketChannel> {
            private SSLContext sslContext;
            private BlockingQueue<Message> notify;

            public LocalServerInitializer (SSLContext sslContext, BlockingQueue<Message> notify) {
                this.sslContext = sslContext;
                this.notify = notify;
            }

            public void initChannel(SocketChannel sc) {
                SSLEngine sslEngine = sslContext.createSSLEngine();
                SslHandler sslHandler = new SslHandler(sslEngine);
                sc.pipeline().addLast(sslHandler);


                sc.pipeline().addLast();

                ConnectedMessage connectedMessage = new ConnectedMessage(sc.pipeline().channel(), null);

                try {
                    notify.put(connectedMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

        }

        private static class LocalHandler extends ChannelInboundHandlerAdapter {
            private static Logger logger = Logger.getLogger(com.ltsllc.miranda.network.Network.LocalHandler.class);

            private static Gson ourGson = new Gson();

            public LocalHandler (BlockingQueue<Message> notify) {
                this.notify = notify;
            }

            private BlockingQueue<Message> notify;

            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                ByteBuf byteBuf = (ByteBuf) msg;
                byte[] array = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(0, array);
                String s = new String(array);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(array);
                InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);

                WireMessage wireMessage = ourGson.fromJson(inputStreamReader, WireMessage.class);

                NetworkMessage networkMessage = new NetworkMessage(null, wireMessage);

                try {
                    notify.put(networkMessage);
                } catch (Exception e) {
                    logger.fatal("exception", e);
                    System.exit(1);
                }
            }

            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                logger.error("Exception caught", cause);
                ctx.close();
            }
        }

        private static class LocalInitializer extends ChannelInitializer<SocketChannel> {
            private Logger logger = Logger.getLogger(com.ltsllc.miranda.network.Network.LocalInitializer.class);

            private SslContext sslContext;
            private BlockingQueue<Message> notify;

            public LocalInitializer(SslContext sslContext, BlockingQueue<Message> notify) {
                this.sslContext = sslContext;
                this.notify = notify;
            }

            public void initChannel(SocketChannel sc) {
                logger.info ("got connection to " + sc.remoteAddress());

                SslHandler sslHandler = sslContext.newHandler(sc.alloc());
                sc.pipeline().addLast(sslHandler);
                com.ltsllc.miranda.network.Network.LocalHandler localHandler = new com.ltsllc.miranda.network.Network.LocalHandler(notify);
                sc.pipeline().addLast(localHandler);

                ConnectedMessage connectedMessage = new ConnectedMessage(sc.pipeline().channel(), null);
                try {
                    notify.put(connectedMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.close();
                logger.error("Exception while trying to establish connection", cause);
            }
        }




        private TrustManagerFactory getTrustMangerFactory() {
            FileInputStream fis = null;
            TrustManagerFactory trustManagerFactory = null;

            try {
                String filename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
                String passwordString = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
                fis = new FileInputStream(filename);
                char[] password = null;
                if (null != passwordString)
                    password = passwordString.toCharArray();

                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(fis, password);

                trustManagerFactory.init(keyStore);
            } catch (Exception e) {
                logger.fatal("Exception trying to get TrustManager", e);
                System.exit(1);
            } finally {
                IOUtils.closeNoExceptions(fis);
            }

            return trustManagerFactory;
        }

        private TrustManager getTrustManger() {
            FileInputStream fis = null;
            TrustManagerFactory trustManagerFactory = null;
            TrustManager[] trustManagers = null;

            try {
                String filename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
                String passwordString = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
                fis = new FileInputStream(filename);
                char[] password = null;
                if (null != passwordString)
                    password = passwordString.toCharArray();

                KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                keyStore.load(fis, password);

                trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);
                trustManagers = trustManagerFactory.getTrustManagers();
            } catch (Exception e) {
                logger.fatal("Exception trying to get TrustManager", e);
                System.exit(1);
            } finally {
                IOUtils.closeNoExceptions(fis);
            }

            return trustManagers[0];
        }

        public Network(BlockingQueue<Message> queue) {
            super("Network");

            setQueue(queue);
            setCurrentState(new ReadyState(this));
        }


        private SslContext createClientContext () {
            SslContext sslContext = null;

            try {
                SSLContext defaultContext = SSLContext.getDefault();
                SSLSocketFactory sslSocketFactory = defaultContext.getSocketFactory();
                String[] cipherSuites = sslSocketFactory.getDefaultCipherSuites();
                List<String> ciphers = Arrays.asList(cipherSuites);

                sslContext = SslContextBuilder
                        .forClient()
                        .ciphers(ciphers)
                        .trustManager(getTrustMangerFactory())
                        .build();

            } catch (Exception e) {
                logger.fatal ("Exception trying to create SslConext", e);
                System.exit(1);
            }

            return sslContext;
        }


    public void connectTo(String host, int port, BlockingQueue<Message> notify) {
        try {
            logger.info("connecting to " + host + ":" + port);
            SslContext sslContext = createClientContext();
            ConnectFutureListener connectFutureListener = new ConnectFutureListener(notify, sslContext);
            ChannelFuture cf = getBootstrap().connect(host, port);
            cf.addListener(connectFutureListener);
        } catch (Exception e) {
            logger.fatal("caught exception", e);
            System.exit(1);
        }
    }


        private SslContext createClientSslContext() {
            SslContext sslContext = null;

            try {
                SSLContext defaultContext = SSLContext.getDefault();
                SSLSocketFactory sslSocketFactory = defaultContext.getSocketFactory();
                String[] cipherSuites = sslSocketFactory.getDefaultCipherSuites();
                List<String> ciphers = Arrays.asList(cipherSuites);

                sslContext = SslContextBuilder
                        .forClient()
                        .ciphers(ciphers)
                        .trustManager(getTrustMangerFactory())
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            return sslContext;
        }


        private Bootstrap createClientBootstrap() {
            Bootstrap bootstrap = new Bootstrap();

            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            bootstrap.group(eventLoopGroup);

            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

            return bootstrap;
        }


        public void connectTo(BlockingQueue<Message> sender, String host, int port, BlockingQueue<Message> notify) {
            logger.info("Conecting to " + host + ":" + port);



            try {
                SslContext clientContext = createClientContext();
                Bootstrap bootstrap = createClientBootstrap();

                com.ltsllc.miranda.network.Network.LocalClientChannelInitializer localClientChannelInitializer = new com.ltsllc.miranda.network.Network.LocalClientChannelInitializer(clientContext, sender);
                bootstrap.handler(localClientChannelInitializer);
                bootstrap.connect(host, port).sync();
            } catch (Exception e) {
                logger.error("Exception while trying to connect", e);
            }
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

                com.ltsllc.miranda.network.Network.LocalServerInitializer localServerInitializer = new com.ltsllc.miranda.network.Network.LocalServerInitializer(serverContext, notify);

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
*/
}
