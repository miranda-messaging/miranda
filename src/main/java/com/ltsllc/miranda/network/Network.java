package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.node.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
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
            String className = wireMessage.getClassName();
            Class clazz = getClass().forName(className);

            wireMessage = (WireMessage) ourGson.fromJson(s, clazz);

            NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);
            Consumer.staticSend(networkMessage, notify);
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

            JsonParser jsonParser = new JsonParser(s);

            for (WireMessage wireMessage : jsonParser.getMessages()) {
                NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);
                Consumer.staticSend(networkMessage, notify);
            }

        }
    }

    private static class LocalClientInitializer extends ChannelInitializer<SocketChannel> {
        private SslContext sslContext;

        public LocalClientInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel(SocketChannel sc) {
            if (null != sslContext) {
                SslHandler sslHandler = sslContext.newHandler(sc.alloc());
                sc.pipeline().addLast(sslHandler);
            }

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
                    Consumer.staticSend(connectedMessage, notify);
                } else {
                    ConnectFailedMessage connectFailedMessage = new ConnectFailedMessage(null, channelFuture.cause(), null);
                    Consumer.staticSend(connectFailedMessage, notify);
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
        setCurrentState(new NetworkReadyState((this)));
    }

    public void connectTo (BlockingQueue<Message> notify, String host, int port) {
        try {
            logger.info("Connecting to " + host + ":" +port);

            SslContext sslContext = null;

            MirandaProperties proprties = Miranda.properties;
            MirandaProperties.EncryptionModes mode = proprties.getEncrptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);

            if (mode == MirandaProperties.EncryptionModes.RemoteCA) {
                sslContext = Utils.createClientSslContext();
            } else if (mode == MirandaProperties.EncryptionModes.LocalCA) {
                String trustStoreFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
                String trustStorePassword = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
                String certificateAlias = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS);
                sslContext = Utils.createClientSslContext(trustStoreFilename, trustStorePassword, certificateAlias);
            }

            LocalClientInitializer localClientInitializer = new LocalClientInitializer(sslContext);

            Bootstrap bootstrap = Utils.createClientBootstrap(localClientInitializer);
            LocalClientHandler localClientHandler = new LocalClientHandler(notify);
            bootstrap.handler(localClientHandler);
            ChannelFuture channelFuture = bootstrap.connect(host, port);

            LocalChannelFutureListener localChannelFutureListener = new LocalChannelFutureListener(notify, sslContext);
            channelFuture.addListener(localChannelFutureListener);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}