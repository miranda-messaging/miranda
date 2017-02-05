package com.ltsllc.miranda.network;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.node.*;
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
import java.util.concurrent.BlockingQueue;


/**
 * Created by Clark on 1/31/2017.
 */
public class NetworkListener {
    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        private static Logger logger = Logger.getLogger(LocalHandler.class);
        private static Gson ourGson = new Gson();

        private BlockingQueue<Message> node;

        public LocalHandler (BlockingQueue<Message> node) {
            this.node = node;
        }

        public void channelRead (ChannelHandlerContext channelHandlerContext, Object message) {
            ByteBuf byteBuf = (ByteBuf) message;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            logger.info ("Got " + s);

            WireMessage wireMessage = ourGson.fromJson(s, WireMessage.class);
            NetworkMessage networkMessage = new NetworkMessage(node, wireMessage);
            Consumer.send(networkMessage, node);
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

            Node node = new Node(inetSocketAddress);
            LocalHandler localHandler = new LocalHandler(node.getQueue());
            socketChannel.pipeline().addLast(localHandler);
        }
    }

    private static class LocalChannelListener implements ChannelFutureListener {
        public void operationComplete (ChannelFuture channelFuture) {
            if (!channelFuture.isSuccess())
            {

            }
            else {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channelFuture.channel().remoteAddress();

                Node node = new Node(inetSocketAddress);

                LocalHandler localHandler = new LocalHandler(node.getQueue());
                channelFuture.channel().pipeline().addLast(localHandler);
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
