/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.CipherInputStream;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.X509Certificate;


/**
 * Created by Clark on 2/3/2017.
 */
public class Utils {
    private static Logger logger = Logger.getLogger(Utils.class);


    public static PrivateKey loadKey(String filename, String passwordString, String alias) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = null;
        FileInputStream fileInputStream = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fileInputStream = new FileInputStream(filename);
            keyStore.load(fileInputStream, passwordString.toCharArray());
            privateKey = (PrivateKey) keyStore.getKey(alias, passwordString.toCharArray());
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return privateKey;
    }


    public static X509Certificate loadCertificate(String filename, String passwordString, String alias)
            throws GeneralSecurityException, IOException {
        X509Certificate certificate = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fileInputStream, passwordString.toCharArray());
            certificate = (X509Certificate) keyStore.getCertificate(alias);
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


    public static KeyStore loadKeyStore(String filename, String passwordString)
            throws GeneralSecurityException, IOException {
        KeyStore keyStore = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(filename);
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(fileInputStream, passwordString.toCharArray());
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

    public static void closeIgnoreExceptions(Reader r) {
        if (null != r)
            try {
                r.close();
            } catch (IOException e) {

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

    public static byte[] calculateSha1(FileInputStream fileInputStream)
            throws NoSuchAlgorithmException, IOException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

        byte buffer[] = new byte[BUFFER_SIZE];
        int bytesRead;

        do {
            bytesRead = fileInputStream.read(buffer);
            messageDigest.update(buffer);
        } while (BUFFER_SIZE == bytesRead);

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


    public static String inputStreamToString (InputStream inputStream) throws IOException {
        StringWriter stringWriter = new StringWriter();

        int b = inputStream.read();
        while (-1 != b) {
            String stringByte = byteToHexString((byte) b);
            stringWriter.write(stringByte);
        }

        stringWriter.close();
        return stringWriter.toString();
    }


    public static String cipherStreamToString (CipherInputStream cipherInputStream) throws IOException {
        StringWriter stringWriter = new StringWriter();

        int b = cipherInputStream.read();
        while (-1 != b) {
            String stringByte = byteToHexString((byte) b);
            stringWriter.write(stringByte);
            b = cipherInputStream.read();
        }

        stringWriter.close();
        return stringWriter.toString();

    }


    public static byte[] hexStringToBytes(String hexString) throws IOException {
        StringReader reader = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        reader = new StringReader(hexString);

        char[] buffer = new char[2];

        int bytesRead = reader.read(buffer);

        while (-1 != bytesRead) {
            byte b = toByte(buffer);
            byteArrayOutputStream.write(b);
            bytesRead = reader.read(buffer);
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


    public static String calculateSha1(byte[] buffer) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = null;

        messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(buffer);

        String digest = bytesToString(messageDigest.digest());
        return digest;
    }


    public static String calculateSha1(String s) throws NoSuchAlgorithmException {
        byte[] buffer = s.getBytes();
        return calculateSha1(buffer);
    }

    public static String calculateSha1LogExceptions(String s) {
        try {
            byte[] buffer = s.getBytes();
            return calculateSha1(buffer);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception calculating sha1", e);
        }

        return null;
    }


    public static SslContext createServerSslContext(String serverFilename, String serverPassword, String serverAlias,
                                                    String trustStoreFilename, String trustStorePassword, String trustStoreAlias)
            throws IOException, GeneralSecurityException {
        SslContext sslContext = null;

        PrivateKey privateKey = loadKey(serverFilename, serverPassword, serverAlias);
        X509Certificate certificate = loadCertificate(serverFilename, serverPassword, trustStoreAlias);
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(trustStoreFilename, trustStorePassword);

        sslContext = SslContextBuilder
                .forServer(privateKey, certificate)
                .trustManager(trustManagerFactory)
                .build();

        return sslContext;
    }


    public static TrustManagerFactory createTrustManagerFactory(String filename, String passwordString)
            throws IOException, GeneralSecurityException {
        FileInputStream fileInputStream = null;
        TrustManagerFactory trustManagerFactory = null;

        try {
            fileInputStream = new FileInputStream(filename);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(fileInputStream, passwordString.toCharArray());

            trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
        } finally {
            closeIgnoreExceptions(fileInputStream);
        }

        return trustManagerFactory;
    }

    public static KeyManagerFactory createKeyManagerFactoy(String filename, String password)
            throws IOException, GeneralSecurityException {
        KeyManagerFactory keyManagerFactory = null;

        KeyStore keyStore = loadKeyStore(filename, password);
        keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password.toCharArray());

        return keyManagerFactory;
    }

    public static Bootstrap createClientBootstrap(ChannelInitializer<SocketChannel> channelInitializer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.handler(channelInitializer);

        return bootstrap;
    }

    public static SslContext createClientSslContext(String filename, String truststorePassword)
            throws IOException, GeneralSecurityException {
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(filename, truststorePassword);

        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(trustManagerFactory)
                .build();

        return sslContext;
    }

    public static SSLContext createSocketServerSslContext(String serverFilename, String serverPassword, String serverAlias,
                                                          String trustStoreFilename, String trustStorePassword, String trustStoreAlias)
            throws GeneralSecurityException, IOException {
        PrivateKey key = loadKey(serverFilename, serverPassword, serverAlias);
        X509Certificate certificate = loadCertificate(trustStoreFilename, trustStorePassword, trustStoreAlias);

        SSLContext sslContext = SSLContext.getDefault();
        return sslContext;
    }

    public static String hexStringToString(String hexString) throws IOException {
        byte[] bytes = hexStringToBytes(hexString);
        return new String(bytes);
    }

    public static byte[] toBytes (long value) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(Long.BYTES);
        byteBuffer.putLong(value);
        return byteBuffer.array();
    }

    public static java.security.PublicKey pemStringToPublicKey (String pemString) {
        try {
            StringReader stringReader = new StringReader(pemString);
            PEMParser pemParser = new PEMParser(stringReader);
            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
            return converter.getPublicKey(subjectPublicKeyInfo);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String publicKeyToPemString (java.security.PublicKey publicKey) {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(publicKey);
            pemWriter.close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String createPublicKeyPem () throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return publicKeyToPemString(keyPair.getPublic());
    }
}
