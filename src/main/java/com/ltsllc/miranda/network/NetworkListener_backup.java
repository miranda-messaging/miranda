package com.ltsllc.miranda.network;

import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.util.IOUtils;
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

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Clark on 1/20/2017.
 */
public class NetworkListener_backup implements Runnable {

    private Logger logger = Logger.getLogger(NetworkListener_backup.class);


    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        private String message;

        public LocalHandler (String message) {
            this.message = message;
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] array = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, array);
            String s = new String(array);

            if (s.equals(message))
                System.out.println("It worked!");
            else
                System.out.println("Failure");

            System.exit(0);
        }
    }



    public static class LocalInitializer extends ChannelInitializer<SocketChannel> {
        private Logger logger = Logger.getLogger(LocalInitializer.class);

        private SslContext sslContext;
        private String message;

        public LocalInitializer(SslContext sslContext, String message) {
            this.sslContext = sslContext;
            this.message = message;
        }

        public void initChannel(SocketChannel sc) {
            logger.info ("got connection");

            SslHandler sslHandler = sslContext.newHandler(sc.alloc());
            sc.pipeline().addLast(sslHandler);
            LocalHandler localHandler = new LocalHandler(message);
            sc.pipeline().addLast(localHandler);
        }
    }


    public NetworkListener_backup(int port) {
        this.port = port;
    }

    private int port;
    private ChannelInitializer<SocketChannel> childHandler;
    private Thread thread;

    public int getPort() {
        return port;
    }

    public ChannelInitializer<SocketChannel> getChildHandler() {
        return childHandler;
    }

    public void setChildHandler(ChannelInitializer<SocketChannel> childHandler) {
        this.childHandler = childHandler;
    }

    private KeyManager[] getKeyManager () {
        FileInputStream fis = null;
        KeyStore keyStore = null;
        KeyManager[] keyManagers = null;

        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            String filename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            char[] password = null;
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            if (null != passwordString)
                password = passwordString.toCharArray();

            fis = new FileInputStream(filename);
            keyStore.load(fis, password);
            kmf.init (keyStore, password);
            keyManagers = kmf.getKeyManagers();
        }  catch (Exception e) {
            logger.fatal ("Exception trying to get truststore", e);
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fis);
        }

        return keyManagers;
    }


    private KeyManager[] getKeyManagers () {
        FileInputStream fis = null;
        KeyStore keyStore = null;
        KeyManager[] keyManagers = null;

        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            String filename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            char[] password = null;
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            if (null != passwordString)
                password = passwordString.toCharArray();

            fis = new FileInputStream(filename);
            keyStore.load(fis, password);
            kmf.init (keyStore, password);
            keyManagers = kmf.getKeyManagers();
        }  catch (Exception e) {
            logger.fatal ("Exception trying to get truststore", e);
            System.exit(1);
        }

        return keyManagers;
    }


    private TrustManager[] getTrustMangers () {
        KeyStore keyStore = null;
        FileInputStream fis = null;
        TrustManager[] trustManagers = null;

        String trustStoreFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String passwordString = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);

        char[] password = null;
        if (null != passwordString)
            password = passwordString.toCharArray();

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new FileInputStream(trustStoreFilename);
            keyStore.load (fis, password);

            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();

        } catch (Exception e) {
            logger.fatal ("Error trying to load server keystore", e);
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fis);
        }

        return trustManagers;
    }




    public void listen () {
        try {
            logger.info("Listening on port " + getPort());

            ServerBootstrap serverBootstrap = createServerBootstrap();
            serverBootstrap.bind(getPort());
        } catch (Exception e) {
            logger.fatal ("Exception thrown while trying to listen", e);
        }
    }

    public void start () {
        thread = new Thread (this);
        thread.start();
    }

    public void run () {
        listen();
    }

    private KeyStore getSeverKeyStore () {
        FileInputStream fis = null;

        String serverKeystoreFilename = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
        KeyStore keyStore = null;

        String passwordString = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);
        char[] password = null;
        if (null != passwordString)
            password = passwordString.toCharArray();

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fis = new FileInputStream(serverKeystoreFilename);
            keyStore.load (fis, password);
        } catch (Exception e) {
            logger.fatal ("Error trying to load server keystore", e);
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fis);
        }

        return keyStore;
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

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(fis, password);
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            logger.fatal("Exception trying to get TrustManager", e);
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fis);
        }

        return trustManagerFactory;
    }



    private SslContext createServerContext () {
        SslContext sslContext = null;

        try {
            PrivateKey privateKey = loadPrivateKey();
            X509Certificate certificate = loadCertificate();

            SSLContext defaultContext = SSLContext.getDefault();
            SSLSocketFactory sslSocketFactory = defaultContext.getSocketFactory();
            String[] cipherSuites = sslSocketFactory.getDefaultCipherSuites();
            List<String> ciphers = Arrays.asList(cipherSuites);

            sslContext = SslContextBuilder
                    .forServer(privateKey, certificate)
                    .ciphers(ciphers)
                    .trustManager(getTrustMangerFactory())
                    .build();
        } catch (Exception e) {
            logger.fatal ("Exception trying to create t", e);
            System.exit(1);
        }

        return sslContext;
    }
/*
    private ServerBootstrap createBootstrap2 () {
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

            SSLEngine sslEngine = serverContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            SslHandler sslHandler = new SslHandler(sslEngine);
            SslServer.LocalHandler localHandler = new SslServer.LocalHandler(sslHandler);

            serverBootstrap.childHandler(localHandler);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return serverBootstrap;
    }
    */


    private ServerBootstrap createServerBootstrap () {
        ServerBootstrap serverBootstrap = null;

        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup);
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            serverBootstrap.channel(NioServerSocketChannel.class);


            LocalInitializer localInitializer = new LocalInitializer(createServerContext(), "hello world");
            serverBootstrap.childHandler(localInitializer);


            /*

            serverBootstrap.childHandler(sslHandler);
            */
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return serverBootstrap;
    }




    private X509Certificate loadCertificate() {
        X509Certificate certificate = null;

        try {
            String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);

            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fin = new FileInputStream(keyStoreFilename);
            ks.load(fin, passwordString.toCharArray());

            certificate = (X509Certificate) ks.getCertificate("ca");
        } catch (Exception e) {
            logger.fatal ("exception during test", e);
            System.exit(1);
        }

        return certificate;
    }




    private PrivateKey loadPrivateKey () {
        PrivateKey privateKey = null;

        try {
            String keyStoreFilename = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);

            KeyStore ks = KeyStore.getInstance("JKS");
            FileInputStream fin = new FileInputStream(keyStoreFilename);
            ks.load(fin, passwordString.toCharArray());

            Key k = ks.getKey("server", passwordString.toCharArray());
            privateKey = (PrivateKey) k;
        } catch (Exception e) {
            logger.fatal ("Exception trying to load SSL", e);
            System.exit(1);
        }

        return privateKey;
    }

    private KeyManager[] createKeyManagers () {
        FileInputStream fis = null;
        KeyManager[] keyManagers = null;

        try {
            String passwordString = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE_PASSWORD);
            char[] password = passwordString.toCharArray();

            String keystoreFilename = System.getProperty(MirandaProperties.PROPERTY_SERVER_KEYSTORE);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = getKeyStore(keystoreFilename, passwordString);
            kmf.init(keyStore, password);
            keyManagers = kmf.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return keyManagers;
    }


    private KeyStore getKeyStore(String filename, String passwordString) {
        KeyStore keyStore = null;
        FileInputStream fis = null;

        try {
            char[] password = null;
            if (null != passwordString)
                password = passwordString.toCharArray();

            fis = new FileInputStream(filename);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fis, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fis);
        }

        return keyStore;
    }


    private TrustManager[] getTrustManagers() {
        TrustManager[] trustManagers = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = getKeyStore("truststore", "whatever");
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagers;
    }
}
