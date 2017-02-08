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

        private SslContext sslContext;

        public LocalInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel (SocketChannel socketChannel) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.remoteAddress();
            logger.info("Got connection from " + inetSocketAddress);

            SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
            // socketChannel.pipeline().addLast(sslHandler);

            Node node = new Node(inetSocketAddress, socketChannel);
            node.start();

            LocalHandler localHandler = new LocalHandler(node.getQueue());
            socketChannel.pipeline().addLast(localHandler);

       }
    }

    private static class LocalChannelListener implements ChannelFutureListener {
        private static Logger logger = Logger.getLogger(LocalChannelListener.class);

        public void operationComplete (ChannelFuture channelFuture) {
            if (channelFuture.isSuccess())
            {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channelFuture.channel().remoteAddress();

                Node node = new Node(channelFuture.channel());
                node.start();

                LocalHandler localHandler = new LocalHandler(node.getQueue());
                channelFuture.channel().pipeline().addLast(localHandler);

                NodeAddedMessage nodeAddedMessage = new NodeAddedMessage(null, node);
                Consumer.staticSend(nodeAddedMessage, Cluster.getInstance().getQueue());
            }

        }

    }

    private Logger logger = Logger.getLogger(NetworkListener.class);

    private int port;

    public NetworkListener (int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }


    public void listen() {
        String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE);
        String keyStorePassword = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_PASSWORD);
        String keyStoreAlias = System.getProperty(MirandaProperties.PROPERTY_KEY_STORE_ALIAS);

        String certificateFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String certificatePassword = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
        String certificateAlias = System.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);

        SslContext sslContext = Utils.createServerSslContext(keyStoreFilename, keyStorePassword, keyStoreAlias, certificateFilename, certificatePassword, certificateAlias);
        LocalInitializer localInitializer = new LocalInitializer(sslContext);

        ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localInitializer);

        logger.info ("listening at " + port);
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        LocalChannelListener localChannelListener = new LocalChannelListener();
        channelFuture.addListener(localChannelListener);
    }
}
