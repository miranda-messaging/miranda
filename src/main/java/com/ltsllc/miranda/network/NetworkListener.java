package com.ltsllc.miranda.network;

import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.util.IOUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;

import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.security.*;
import java.security.cert.*;
import java.util.Arrays;
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
        private SslContext sslContext;

        public LocalInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel (SocketChannel socketChannel) {
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


    private List<String> getDefaultCiphers()
    {
        List<String> ciphers = null;

        try {
            SSLContext context = SSLContext.getDefault();
            SSLSocketFactory sf = context.getSocketFactory();
            String[] cipherSuites = sf.getSupportedCipherSuites();
            ciphers = Arrays.asList(cipherSuites);
        } catch (NoSuchAlgorithmException e) {
            logger.fatal("Exception trying to get default algorithms", e);
            System.exit(1);
        }

        return ciphers;
    }


    public int getPort() {
        return port;
    }

    private SslContext createServerContext() {
        SslContext sslCtx = null;

        try {
            String keyStoreFilePath = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
            String keyStorePassword = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);

            String trustStoreFileName = "c:\\users\\clark\\IdeaProjects\\miranda\\data\\ca-certificate.pem.txt";
            File trustStoreFile = new File(trustStoreFileName);

            String certFilePath = "c:\\users\\clark\\IdeaProjects\\miranda\\data\\server.cer";

            PrivateKey k = loadKey(keyStoreFilePath, keyStorePassword, "server");
            X509Certificate c = loadCert(certFilePath);

            PrivateKey privateKey = loadKey(keyStoreFilePath, keyStorePassword, "server");

            TrustManagerFactory trustManagerFactory = createTrustManagerFactory();

            sslCtx = SslContextBuilder
                    .forServer(privateKey, c)
                    .ciphers(getDefaultCiphers())
                    .trustManager(trustManagerFactory)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslCtx;

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


    public void listen() {
        ServerBootstrap serverBootstrap = createServerBootstrap();
        logger.info ("listening at " + port);
        ChannelFuture channelFuture = serverBootstrap.bind(port);
        channelFuture.addListener(new LocalChannelListener());
    }

    public static PrivateKey loadKey (String filename, String password, String alias) throws Exception
    {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        FileInputStream fin = new FileInputStream(filename);
        ks.load(fin, password.toCharArray());

        Key k = ks.getKey(alias, password.toCharArray());
        return (PrivateKey) k;
    }


    public static X509Certificate loadCert(String filename) throws Exception
    {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        FileInputStream in = new FileInputStream(filename);
        java.security.cert.Certificate c = cf.generateCertificate(in);
        in.close();
        return (X509Certificate) c;
    }


}
