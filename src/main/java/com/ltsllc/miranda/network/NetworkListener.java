package com.ltsllc.miranda.network;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.WireMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

            if (null != sslContext) {
                SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
                socketChannel.pipeline().addLast(sslHandler);
            }

            Node node = new Node(inetSocketAddress, socketChannel);
            node.start();

            NewConnectionMessage newConnectionMessage = new NewConnectionMessage(null, this, node);
            Consumer.staticSend(newConnectionMessage, Miranda.getInstance().getQueue());

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

                // if (channelFuture.channel() instanceof NioServerSocketChannel) {
                    // logger.warn("Spurious connection");
                    // return;
                // }


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
                Consumer.staticSend(newConnectionMessage, Miranda.getInstance().getQueue());
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
        MirandaProperties properties = MirandaProperties.getInstance();
        MirandaProperties.EncryptionModes mode = properties.getEncrptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        SslContext sslContext = null;

        if (mode == MirandaProperties.EncryptionModes.LocalCA || mode == MirandaProperties.EncryptionModes.RemoteCA)
        {
            String keyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            String keyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            String keyStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS);

            String certificateFilename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String certificatePassword = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            String certificateAlias = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS);

            sslContext = Utils.createServerSslContext(keyStoreFilename, keyStorePassword, keyStoreAlias, certificateFilename, certificatePassword, certificateAlias);
        }

        LocalInitializer localInitializer = new LocalInitializer(sslContext);

        ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localInitializer);

        logger.info ("listening at " + port);
        serverBootstrap.bind(port);
        ChannelFuture channelFuture = null;

        try {
            // channelFuture = serverBootstrap.bind(port).sync();
            channelFuture = serverBootstrap.bind(port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LocalChannelListener localChannelListener = new LocalChannelListener();
        channelFuture.addListener(localChannelListener);
    }
}
