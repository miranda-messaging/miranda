package com.ltsllc.miranda;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Clark on 2/3/2017.
 */
public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);


    public static PrivateKey loadKey (String filename, String passwordString, String alias)
    {
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


    public static X509Certificate loadCertificate (String filename, String passwordString, String alias)
    {
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




    public static KeyStore loadKeyStore (String filename, String passwordString) {
        KeyStore keyStore = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(fileInputStream, passwordString.toCharArray());

        } catch (Exception e) {
            logger.fatal ("Exception trying to load key store", e);
            System.exit(1);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return keyStore;
    }



    public static void closeIgnoreExceptions (InputStream inputStream)
    {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {}
        }
    }


    private static final int BUFFER_SIZE = 8192;

    public static byte[] calculateSha1 (FileInputStream fileInputStream) {
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
            logger.fatal ("Exception trying to calculate sha1", e);
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


    public static String bytesToString (byte[] bytes) {
        StringWriter stringWriter = new StringWriter();

        for (byte b : bytes) {
            stringWriter.append(byteToHexString(b));
        }

        return stringWriter.toString();
    }
}
