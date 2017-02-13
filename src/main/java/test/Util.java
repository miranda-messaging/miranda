package test;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;

import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * Created by Clark on 2/4/2017.
 */
public class Util {
    private static Logger logger = Logger.getLogger(Util.class);


    public static ServerBootstrap createServerBootstrap (ChannelHandler channelHandler) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(channelHandler);

        return serverBootstrap;
    }


    public static Bootstrap createClientBootstrap (ChannelHandler channelHandler) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(channelHandler);

        return bootstrap;
    }



    public static TrustManagerFactory createTrustManagerFactory(String filename, String passwordString) {
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


    public static SslContext createClientSslContext(String filename, String passwordString, String alias) {
        SslContext sslContext = null;

        try {
            TrustManagerFactory trustManagerFactory = createTrustManagerFactory(filename, passwordString);

            sslContext = SslContextBuilder
                    .forClient()
                    //.ciphers(getDefaultCiphers())
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }


    public static SslContext createServerSslContext(String keyFilename, String keyPassword, String keyAlias,
                                                    String trustStoreFilename, String trustStorePassword, String trustStoreAlias) {
        SslContext sslContext = null;

        try {
            TrustManagerFactory trustManagerFactory = createTrustManagerFactory(trustStoreFilename, trustStorePassword);
            PrivateKey privateKey = getPrivateKey (keyFilename, keyPassword, keyAlias);
            X509Certificate certificate = getCertificate (trustStoreFilename, trustStorePassword, trustStoreAlias);

            sslContext = SslContextBuilder
                    .forServer(privateKey, certificate)
                    //.ciphers(getDefaultCiphers())
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }


    public static PrivateKey getPrivateKey (String filename, String password, String alias) {
        KeyStore keyStore = getKeyStore(filename, password);
        PrivateKey privateKey = null;

        try {
            privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            logger.fatal ("Exception trying to get private key", e);
            System.exit(1);
        }

        return privateKey;
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
            closeIgnoreExceptions(fis);
        }

        return keyStore;
    }


    public static X509Certificate getCertificate (String filename, String password, String alias)
    {
        X509Certificate certificate = null;
        KeyStore keyStore = getKeyStore(filename, password);

        try {
            certificate = (X509Certificate) keyStore.getCertificate(alias);
        } catch (Exception e) {
            logger.fatal ("Exception trying to get certificate", e);
            System.exit(1);
        }

        return certificate;
    }
}
