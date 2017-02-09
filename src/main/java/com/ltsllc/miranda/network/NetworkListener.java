package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.WireMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 1/31/2017.
 */
public class NetworkListener {
    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = Logger.getLogger(LocalHandler.class);
        private BlockingQueue<Message> node;

        public LocalHandler (BlockingQueue<Message> node) {
            this.node = node;
        }

        public void channelRead (ChannelHandlerContext channelHandlerContext, Object message) {
            ByteBuf byteBuf = (ByteBuf) message;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            logger.info("Got " + s);

            JsonParser jsonParser = new JsonParser(s);

            for (WireMessage wireMessage : jsonParser.getMessages()) {
                NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);
                Consumer.staticSend(networkMessage, node);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.close();

            ConnectionClosedMessage connectionClosedMessage = new ConnectionClosedMessage(null, this);
            Consumer.staticSend(connectionClosedMessage, node);
        }
    }


    private static class LocalInitializer extends ChannelInitializer<SocketChannel> {
        private static Logger logger = Logger.getLogger(LocalInitializer.class);

        private BlockingQueue<Message> cluster;
        private SslContext sslContext;

        public LocalInitializer (SslContext sslContext, BlockingQueue<Message> cluster) {
            this.sslContext = sslContext;
            this.cluster = cluster;
        }

        public void initChannel (SocketChannel socketChannel) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.remoteAddress();
            logger.info("Got connection from " + inetSocketAddress);

            SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
            // socketChannel.pipeline().addLast(sslHandler);

            Node node = new Node(inetSocketAddress, socketChannel);
            node.start();

            NewConnectionMessage newConnectionMessage = new NewConnectionMessage(null, this, node);
            Consumer.staticSend(newConnectionMessage, cluster);

            LocalHandler localHandler = new LocalHandler(node.getQueue());
            socketChannel.pipeline().addLast(localHandler);
       }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("Exception caught, closing channel", cause);
            ctx.close();
        }
    }

    private static class LocalChannelListener implements ChannelFutureListener {
        private static Logger logger = Logger.getLogger(LocalChannelListener.class);

        public void operationComplete (ChannelFuture channelFuture) {
            if (channelFuture.isSuccess())
            {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channelFuture.channel().remoteAddress();

                //
                // for some reason, we occasionally get empty connections
                //
                if (null == inetSocketAddress) {
                    logger.error("spurious connection");
                    return;
                }

                Node node = new Node(channelFuture.channel());
                node.start();

                LocalHandler localHandler = new LocalHandler(node.getQueue());
                channelFuture.channel().pipeline().addLast(localHandler);

                NewConnectionMessage newConnectionMessage = new NewConnectionMessage(null, this, node);
                Consumer.staticSend(newConnectionMessage, Cluster.getInstance().getQueue());
            }

        }

    }

    private Logger logger = Logger.getLogger(NetworkListener.class);

    private int port;
    private BlockingQueue<Message> cluster;

    public NetworkListener (int port, BlockingQueue<Message> cluster) {
        this.port = port;
        this.cluster = cluster;
    }

    public int getPort() {
        return port;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public void listen() {
        String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE);
        String keyStorePassword = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_PASSWORD);
        String keyStoreAlias = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_ALIAS);

        String certificateFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String certificatePassword = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
        String certificateAlias = System.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);

        SslContext sslContext = Utils.createServerSslContext(keyStoreFilename, keyStorePassword, keyStoreAlias, certificateFilename, certificatePassword, certificateAlias);
        LocalInitializer localInitializer = new LocalInitializer(sslContext, getCluster());

        ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localInitializer);

        logger.info ("listening at " + port);
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        LocalChannelListener localChannelListener = new LocalChannelListener();
        channelFuture.addListener(localChannelListener);
    }
}
