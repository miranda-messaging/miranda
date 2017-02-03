package com.ltsllc.miranda.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clark on 1/31/2017.
 */
public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);


    public static TrustManagerFactory createTrustManagerFactory(String filename, String passwordString, String alias) {
        FileInputStream fileInputStream = null;
        TrustManagerFactory trustManagerFactory = null;

        try {
            fileInputStream = new FileInputStream(filename);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fileInputStream, passwordString.toCharArray());

            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return trustManagerFactory;
    }


    public static void closeIgnoreExceptions (InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
    }


    public static ServerBootstrap createServerBootstrap (ChannelHandler channelHandler) {
        ServerBootstrap serverBootstrap = null;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.channel(NioServerSocketChannel.class);

        serverBootstrap.childHandler(channelHandler);

        return serverBootstrap;
    }




    public static List<String> getDefaultCiphers () {
        List<String> defaultCiphers = null;

        try {
            SSLContext sslContext = SSLContext.getDefault();
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            String[] cipherSuites = sslSocketFactory.getDefaultCipherSuites();
            defaultCiphers = Arrays.asList(cipherSuites);
        } catch (Exception e) {
            logger.fatal ("Exception trying to get default ciphers", e);
            System.exit(1);
        }

        return defaultCiphers;
    }



    public static SslContext createServerSslContext(String filename, String passwordString, String alias) {
        SslContext sslContext = null;

        try {
            TrustManagerFactory trustManagerFactory = createTrustManagerFactory(filename, passwordString, alias);

            sslContext = SslContextBuilder
                    .forClient()
                    .ciphers(getDefaultCiphers())
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }


    public static void close(FileInputStream fileInputStream) {
        if (null != fileInputStream) {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }


    public static KeyStore getKeyStore(String filename, String passwordString) {
        KeyStore keyStore = null;
        FileInputStream fis = null;

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] password = null;
            if (null != passwordString)
                password = passwordString.toCharArray();

            fis = new FileInputStream(filename);
            keyStore.load(fis, password);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            close(fis);
        }

        return keyStore;
    }

    public static TrustManager[] getTrustManagers() {
        TrustManager[] trustManagers = null;

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = getKeyStore("data/truststore", "whatever");
            trustManagerFactory.init(keyStore);
            trustManagers = trustManagerFactory.getTrustManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagers;
    }

    public static TrustManager getTrustManager () {
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

        return trustManagers[0];
    }

    public static TrustManagerFactory createTrustManagerFactory () {
        TrustManagerFactory trustManagerFactory = null;

        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = getKeyStore("data/truststore", "whatever");
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagerFactory;
    }

    public static KeyManager[] createKeyManagers () {
        FileInputStream fis = null;
        KeyManager[] keyManagers = null;

        try {
            String passwordString = "whatever";
            char[] password = passwordString.toCharArray();
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            KeyStore keyStore = getKeyStore("data/serverkeystore", passwordString);
            kmf.init(keyStore, password);
            keyManagers = kmf.getKeyManagers();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return keyManagers;
    }


}

