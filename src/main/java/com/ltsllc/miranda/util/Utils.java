package com.ltsllc.miranda.util;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;


/**
 * Created by Clark on 2/3/2017.
 */
public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);


    public static PrivateKey loadKey(String filename, String passwordString, String alias) {
        PrivateKey privateKey = null;
        FileInputStream fileInputStream = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fileInputStream = new FileInputStream(filename);
            keyStore.load(fileInputStream, passwordString.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(alias, passwordString.toCharArray());
        } catch (Exception e) {
            logger.fatal("Exception while trying to load key", e);
            System.exit(1);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return privateKey;
    }


    public static X509Certificate loadCertificate(String filename, String passwordString, String alias) {
        X509Certificate certificate = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fileInputStream, passwordString.toCharArray());
            certificate = (X509Certificate) keyStore.getCertificate(alias);
        } catch (Exception e) {
            logger.fatal("Exception trying to load certificate", e);
            System.exit(1);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return certificate;
    }


    public static ServerBootstrap createServerBootstrap(ChannelHandler channelHandler) {
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


    public static KeyStore loadKeyStore(String filename, String passwordString) {
        KeyStore keyStore = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(fileInputStream, passwordString.toCharArray());

        } catch (Exception e) {
            logger.fatal("Exception trying to load key store", e);
            System.exit(1);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return keyStore;
    }


    public static void closeIgnoreExceptions(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeIgnoreExceptions(Writer writer) {
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(Socket socket) {
        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(ChannelHandlerContext channelHandlerContext) {
        if (null != channelHandlerContext) {
            channelHandlerContext.close();
        }
    }

    public static void closeLogExceptions(InputStream inputStream, Logger logger) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("Exception closing input stream", e);
            }
        }
    }

    public static void closeLogExceptions(OutputStream outputStream, Logger logger) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.error("Exception closing output stream", e);
            }
        }
    }

    public static void closeLogExceptions(Socket socket, Logger logger) {
        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Exception trying to close socket", e);
            }
        }
    }

    private static final int BUFFER_SIZE = 8192;

    public static byte[] calculateSha1(FileInputStream fileInputStream) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");

            byte buffer[] = new byte[BUFFER_SIZE];
            int bytesRead;

            do {
                bytesRead = fileInputStream.read(buffer);
                messageDigest.update(buffer);
            } while (BUFFER_SIZE == bytesRead);

        } catch (Exception e) {
            logger.fatal("Exception trying to calculate sha1", e);
            System.exit(1);
        }

        return messageDigest.digest();
    }


    private static char[] DIGITS = "0123456789ABCDEF".toCharArray();

    public static String byteToHexString(byte b) {
        StringBuffer sb = new StringBuffer();

        int value = b & 0xf0;
        value = value >> 4;
        sb.append(DIGITS[value]);

        value = b & 0xf;
        sb.append(DIGITS[value]);

        return sb.toString();
    }


    public static String bytesToString(byte[] bytes) {
        StringWriter stringWriter = new StringWriter();

        for (byte b : bytes) {
            stringWriter.append(byteToHexString(b));
        }

        return stringWriter.toString();
    }


    public static byte[] hexStringToBytes(String hexString) {
        StringReader reader = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            reader = new StringReader(hexString);

            char[] buffer = new char[2];

            int bytesRead = reader.read(buffer);

            while (-1 != bytesRead) {
                byte b = toByte(buffer);
                byteArrayOutputStream.write(b);
                bytesRead = reader.read(buffer);
            }
        } catch (IOException e) {
            logger.fatal("Exception trying to convert string to bytes", e);
            System.exit(1);
        }

        return byteArrayOutputStream.toByteArray();
    }


    public static byte toByte(char[] buffer) {
        int value = toNibble(buffer[0]);
        value = value << 4;
        int temp = 0xF & toNibble(buffer[1]);
        value = value | temp;

        return (byte) value;
    }


    public static int toNibble(char c) {
        if (c >= '0' && c <= '9')
            return (c - '0');
        else if (c >= 'A' && c <= 'F')
            return (c - 'A' + 10);
        else {
            throw new IllegalArgumentException();
        }
    }


    public static String calculateSha1(byte[] buffer) {
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance("SHA");
            messageDigest.update(buffer);
        } catch (NoSuchAlgorithmException e) {
            logger.fatal("Exception trying to calculate sha1", e);
            System.exit(1);
        }

        String digest = bytesToString(messageDigest.digest());
        return digest;
    }


    public static String calculateSha1(String s) {
        byte[] buffer = s.getBytes();
        return calculateSha1(buffer);
    }


    public static SslContext createServerSslContext(String serverFilename, String serverPassword, String serverAlias,
                                                    String trustStoreFilename, String trustStorePassword, String trustStoreAlias) {
        SslContext sslContext = null;

        try {
            PrivateKey privateKey = loadKey(serverFilename, serverPassword, serverAlias);
            X509Certificate certificate = loadCertificate(serverFilename, serverPassword, trustStoreAlias);
            TrustManagerFactory trustManagerFactory = createTrustManagerFactory(trustStoreFilename, trustStorePassword);

            sslContext = SslContextBuilder
                    .forServer(privateKey, certificate)
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            logger.fatal("Exception while trying to create SslContext", e);
            System.exit(1);
        }

        return sslContext;
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

    public static KeyManagerFactory createKeyManagerFactoy(String filename, String password) {
        KeyManagerFactory keyManagerFactory = null;

        try {
            KeyStore keyStore = loadKeyStore(filename, password);
            keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());
        } catch (Exception e) {
            logger.fatal("Exception while trying to get key manager factory", e);
            System.exit(1);
        }

        return keyManagerFactory;
    }

    public static Bootstrap createClientBootstrap(ChannelInitializer<SocketChannel> channelInitializer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.handler(channelInitializer);

        return bootstrap;
    }

    public static SslContext createClientSslContext(String filename, String password) throws SSLException {
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(filename, password);

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(trustManagerFactory)
                .build();

        return sslContext;
    }

    public static SSLContext createSocketServerSslContext(String serverFilename, String serverPassword, String serverAlias,
                                                          String trustStoreFilename, String trustStorePassword, String trustStoreAlias)
            throws NoSuchAlgorithmException {
        PrivateKey key = loadKey(serverFilename, serverPassword, serverAlias);
        X509Certificate certificate = loadCertificate(trustStoreFilename, trustStorePassword, trustStoreAlias);

        SSLContext sslContext = SSLContext.getDefault();
        return sslContext;
    }
}
