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
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.log4j.Logger;

import sun.security.pkcs10.PKCS10;
import sun.security.tools.keytool.CertAndKeyGen;
import sun.security.x509.X500Name;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

/**
 * Created by Clark on 2/4/2017.
 */
public class Util {
    private static Logger logger = Logger.getLogger(Util.class);

    public static ServerBootstrap createServerBootstrap(ChannelHandler channelHandler) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        serverBootstrap.group(bossGroup, workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        serverBootstrap.childHandler(channelHandler);

        return serverBootstrap;
    }


    public static Bootstrap createClientBootstrap(ChannelHandler channelHandler) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(channelHandler);

        return bootstrap;
    }


    public static TrustManagerFactory loadTrustStore (KeyStore keyStore) {
        FileInputStream fileInputStream = null;
        TrustManagerFactory trustManagerFactory = null;

        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagerFactory;
    }


    public static void closeIgnoreExceptions(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }


    public static void closeIgnoreExceptions (OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
            }
        }
    }


    public static SslContext createClientSslContext(X509Certificate certificate) {
        SslContext sslContext = null;

        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(certificate)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }

    public static SslContext createClientSslContext(TrustManagerFactory trustManagerFactory) {
        SslContext sslContext = null;

        try {
            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }


    public static TrustManagerFactory createTrustManagerFactory (KeyStore keyStore) {
        TrustManagerFactory trustManagerFactory = null;

        try {
            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return trustManagerFactory;
    }

    public static SslContext createClientSslContext(KeyStore keyStore) {
        SslContext sslContext = null;

        try {
            TrustManagerFactory trustManagerFactory = createTrustManagerFactory(keyStore);

            sslContext = SslContextBuilder
                    .forClient()
                    .trustManager(trustManagerFactory)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }


    public static SslContext createSimpleClientContext() {
        SslContext sslContext = null;

        try {
            sslContext = SslContextBuilder
                    .forClient()
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
            PrivateKey privateKey = getPrivateKey(keyFilename, keyPassword, keyAlias);
            X509Certificate certificate = getCertificate(trustStoreFilename, trustStorePassword, trustStoreAlias);

            sslContext = SslContextBuilder
                    .forServer(privateKey, certificate)
                    .trustManager(certificate)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return sslContext;
    }


    public static PrivateKey getPrivateKey(String filename, String password, String alias) {
        KeyStore keyStore = getKeyStore(filename, password);
        PrivateKey privateKey = null;

        try {
            privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        } catch (Exception e) {
            logger.fatal("Exception trying to get private key", e);
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


    public static X509Certificate getCertificate(String filename, String password, String alias) {
        X509Certificate certificate = null;
        KeyStore keyStore = getKeyStore(filename, password);

        try {
            certificate = (X509Certificate) keyStore.getCertificate(alias);
        } catch (Exception e) {
            logger.fatal("Exception trying to get certificate", e);
            System.exit(1);
        }

        return certificate;
    }


    public static KeyStore getKeyStore(String filename) {
        KeyStore keyStore = null;
        FileInputStream fileInputStream = null;

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            fileInputStream = new FileInputStream(filename);

            keyStore.load(fileInputStream, "".toCharArray());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }
        return keyStore;
    }
}
