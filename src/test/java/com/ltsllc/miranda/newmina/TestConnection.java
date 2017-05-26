package com.ltsllc.miranda.newmina;

import com.ltsllc.miranda.mina.NewMinaNetworkListener;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import io.netty.handler.ssl.SslHandler;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by clarkhobbie on 5/25/17.
 */
public class TestConnection extends TestCase {
    public static class NopHandler extends IoHandlerAdapter {

    }

    public static class EchoHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            System.out.println ("got connection");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            System.out.println("Got message");
            System.out.println(message);
            session.write(message);
        }

        @Override
        public void sessionClosed(IoSession session) throws Exception {
            super.sessionClosed(session);
            System.out.println("connection closed");
        }
    }

    public static class Server implements Runnable {
        private int port;

        public int getPort() {
            return port;
        }

        public Server (int port) {
            this.port = port;
        }

        public void run () {
            System.out.println("server started");

            NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();

            TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory();
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
            nioSocketAcceptor.getFilterChain().addLast("lines", protocolCodecFilter);

            nioSocketAcceptor.setHandler(new EchoHandler());

            InetSocketAddress inetSocketAddress = new InetSocketAddress(getPort());

            try {
                nioSocketAcceptor.bind(inetSocketAddress);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class Client implements Runnable {
        private int port;

        public int getPort() {
            return port;
        }

        public Client (int port) {
            this.port = port;
        }

        @Override
        public void run() {
            System.out.println ("client started");

            NioSocketConnector nioSocketConnector = new NioSocketConnector();

            TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory();
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
            nioSocketConnector.getFilterChain().addLast("lines", protocolCodecFilter);

            LoggingFilter loggingFilter = new LoggingFilter();
            nioSocketConnector.getFilterChain().addLast("logger", loggingFilter);

            nioSocketConnector.setHandler(new NopHandler());

            InetSocketAddress inetSocketAddress = new InetSocketAddress(getPort());
            ConnectFuture connectFuture = nioSocketConnector.connect(inetSocketAddress);
            connectFuture.awaitUninterruptibly();
            IoSession session = connectFuture.getSession();

            Scanner scanner = new Scanner(System.in);
            System.out.print("Client> ");
            String line = scanner.nextLine();
            while (!line.equalsIgnoreCase("quit")) {
                System.out.print("Client> ");
                session.write(line);
                line = scanner.nextLine();
            }
        }
    }

    public static class LocalRunner implements Runnable {
        private Thread thread;
        private Runnable runnable;

        public Runnable getRunnable() {
            return runnable;
        }

        public Thread getThread() {
            return thread;
        }

        public LocalRunner (Runnable runnable) {
            this.thread = new Thread(this);
            this.runnable = runnable;
        }

        public void start () {
            getThread().start();
        }

        public void run() {
            getRunnable().run();
        }
    }

    public static class LocalListenerRunner implements Runnable {
        private NewMinaNetworkListener listener;
        private Thread thread;

        public Thread getThread() {
            return thread;
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public NewMinaNetworkListener getListener() {
            return listener;
        }

        public LocalListenerRunner(NewMinaNetworkListener listener) {
            this.listener = listener;
        }

        public void setListener(NewMinaNetworkListener listener) {
            this.listener = listener;
        }

        public void run() {
            getListener().listen();
        }

        public void start() {
            this.thread = new Thread(this);
            getThread().start();
        }
    }

    private LocalListenerRunner localListenerRunner;
    private NewMinaNetworkListener listener;

    public LocalListenerRunner getLocalListenerRunner() {
        return localListenerRunner;
    }

    public void reset() {
        this.listener = null;
    }

    public static final AttributeKey SSL_HANDLER = new AttributeKey(SslFilter.class, "handler");

    @Before
    public void setup () {
        setuplog4j();
    }

    /*
    @Before
    public void setup() {
        File file = new File("truststore");
        if (!file.exists()) {
            System.err.println("truststore does not exist");
            System.exit(-1);
        }

        System.setProperty("javax.net.ssl.trustStore", file.getPath());
        System.setProperty("javax.net.ssl.trustStorePassword", "whatever");

        setupMiranda();
        setupMirandaFactory("whatever", "whatever");
        setuplog4j();

        this.listener = new NewMinaNetworkListener();
        listener.setAcceptor(new NioSocketAcceptor());
        listener.setPort(6789);

        LocalListenerRunner localListenerRunner = new LocalListenerRunner(listener);
        localListenerRunner.start();
    }
    */

    /*
    @Test
    public void setupConnection() throws Exception {


        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyStore keyStore = Utils.loadKeyStore("keystore", "whatever");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, "whatever".toCharArray());

        KeyManager[] keyManagers = keyManagerFactory.getKey
        sslFilter.setUseClientMode(true);


        connector.getFilterChain().addLast("sslFilter", sslFilter);
        connector.getFilterChain().addLast("lines", texLineCodec);

        pause(50);

        ConnectFuture connectFuture = connector.connect(new InetSocketAddress("localhost", 6789));
        connectFuture.awaitUninterruptibly();
        IoSession session = connectFuture.getSession();

        IoBuffer ioBuffer = IoBuffer.allocate(1024);
        Charset charset = Charset.forName("UTF-8");
        CharsetEncoder encoder = charset.newEncoder();

        ioBuffer.putString("hi there", encoder);
        WriteFuture writeFuture = session.write(ioBuffer);
        writeFuture.awaitUninterruptibly();

        session.write("hi there");

        pause(1000);

    }
    */


    @Test
    public void performTest () {
        LocalRunner client = new LocalRunner(new Client(6789));
        LocalRunner server = new LocalRunner(new Server(6789));

        server.start();
        pause(100);
        client.start();

        pause(20000);
    }

}
