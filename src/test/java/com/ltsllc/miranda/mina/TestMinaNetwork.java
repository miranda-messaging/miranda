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

package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkReadyState;
import com.ltsllc.miranda.test.TestCase;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 3/18/2017.
 */
public class TestMinaNetwork extends TestCase {
    public static final String TEST_MESSAGE = "hi there";

    public static class EchoHandler extends IoHandlerAdapter {
        private Charset charset;

        public EchoHandler () {
            this.charset = Charset.defaultCharset();
        }

        public Charset getCharset() {
            return charset;
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);

            IoBuffer ioBuffer = (IoBuffer) message;
            String s = ioBuffer.getString(getCharset().newDecoder());

            System.out.println("Received: " + s);

            ioBuffer.flip();

            session.write(message);
        }
    }

    public static class LocalTalkerHandler extends IoHandlerAdapter {
        public LocalTalkerHandler () {
            charset = Charset.defaultCharset();
        }

        private Charset charset;

        public Charset getCharset() {
            return charset;
        }

        public void sendMessage (IoSession ioSession) {
            try {
                IoBuffer ioBuffer = IoBuffer.allocate(TEST_MESSAGE.length());
                Charset charset = Charset.defaultCharset();
                ioBuffer.putString(TEST_MESSAGE, charset.newEncoder());
                ioBuffer.flip();
                ioSession.write(ioBuffer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);
            sendMessage(session);
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            sendMessage(session);
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);

            System.out.println("message: " + message);
        }
    }

    public static class LocalHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);

            System.out.println ("Session created");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session, message);

            System.out.println("message: " + message);
        }
    }

    public static class LocalTalker implements Runnable {
        private Thread thread;
        private KeyStore keyStore;
        private KeyStore trustStore;
        private String keyStorePassword;
        private InetSocketAddress inetSocketAddress;

        public InetSocketAddress getInetSocketAddress() {
            return inetSocketAddress;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public KeyStore getTrustStore() {
            return trustStore;
        }

        public KeyStore getKeyStore() {
            return keyStore;
        }

        public Thread getThread() {
            return thread;
        }

        public LocalTalker (KeyStore keyStore, String keyStorePassword, KeyStore trustStore, String host, int port) {
            thread = new Thread(this);
            this.keyStore = keyStore;
            this.trustStore = trustStore;
            this.keyStorePassword = keyStorePassword;
            this.inetSocketAddress = new InetSocketAddress(host, port);
        }

        public void start () {
            thread.start();
        }

        public void run () {
            try {
                basicRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void basicRun () throws Exception {
            NioSocketConnector nioSocketConnector = new NioSocketConnector();
            SslFilter sslFilter = createSslFilter(getKeyStore(), getKeyStorePassword(), getTrustStore());
            sslFilter.setNeedClientAuth(false);
            sslFilter.setUseClientMode(true);
            nioSocketConnector.getFilterChain().addLast("tls", sslFilter);
            LocalTalkerHandler talkerHandler = new LocalTalkerHandler();
            nioSocketConnector.setHandler(talkerHandler);
            ConnectFuture connectFuture = nioSocketConnector.connect(getInetSocketAddress());
            connectFuture.awaitUninterruptibly();
        }
    }

    public static class LocalListener implements Runnable {
        private Thread thread;
        private KeyStore keyStore;
        private KeyStore trustStore;
        private String keyStorePassword;
        private NioSocketAcceptor nioSocketAcceptor;

        public NioSocketAcceptor getNioSocketAcceptor() {
            return nioSocketAcceptor;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public KeyStore getTrustStore() {
            return trustStore;
        }

        public KeyStore getKeyStore() {
            return keyStore;
        }

        public Thread getThread() {
            return thread;
        }

        public LocalListener (KeyStore keyStore, String keyStorePassword, KeyStore trustStore) {
            thread = new Thread(this);
            this.keyStore = keyStore;
            this.trustStore = trustStore;
            this.keyStorePassword = keyStorePassword;
        }

        public void start () {
            thread.start();
        }

        public void run () {
            try {
                basicRun();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public NioSocketAcceptor createAcceptor () throws Exception {
            NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
            SslFilter sslFilter = createSslFilter(getKeyStore(), getKeyStorePassword(), getTrustStore());
            nioSocketAcceptor.getFilterChain().addLast("tls", sslFilter);

            EchoHandler handler = new EchoHandler();
            nioSocketAcceptor.setHandler(handler);

            return nioSocketAcceptor;
        }

        public void basicRun () throws Exception {
            NioSocketAcceptor nioSocketAcceptor = createAcceptor();

            InetSocketAddress inetSocketAddress = new InetSocketAddress(6789);
            nioSocketAcceptor.bind(inetSocketAddress);
            this.nioSocketAcceptor = nioSocketAcceptor;
        }

        public void stop () {
            getThread().interrupt();
            getNioSocketAcceptor().unbind();
        }
    }

    public static class ServerHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            System.out.println("Got connection");
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            String s = message.toString();
            s.trim();
            System.out.println("Got " + s);
            session.write(s);
        }
    }

    private LocalListener localListener;

    @Mock
    private MinaIncomingHandler mockMinaIncomingHadeler;

    @Mock
    private KeyStore mockKeyStore;

    @Mock
    private KeyStore mockTrustStore;

    @Mock
    private IoSession mockIoSession;

    private MinaNetwork minaNetwork;

    public IoSession getMockIoSession() {
        return mockIoSession;
    }

    public LocalListener getLocalListener() {
        return localListener;
    }

    public void setLocalListener(LocalListener localListener) {
        this.localListener = localListener;
    }

    public static SslFilter createSslFilter (KeyStore keyStore, String keyStorePassword, KeyStore trustStore) throws Exception {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                new SecureRandom());

        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setNeedClientAuth(true);

        return sslFilter;
    }



    public void reset() throws MirandaException {
        super.reset();

        mockKeyStore = null;
        mockTrustStore = null;
        mockMinaIncomingHadeler = null;
        mockIoSession = null;
        minaNetwork = null;
    }


    public MinaNetwork getMinaNetwork() {
        return minaNetwork;
    }

    public void setupMinaListener(int port, KeyStore keyStore, KeyStore trustStore, String password) throws Exception {
        LocalListener listener = new LocalListener(keyStore, password, trustStore);
        listener.start();
        setLocalListener(listener);
    }

    @Before
    public void setup() {
        try {

            reset();

            super.setup();

            setuplog4j();

            setupMirandaProperties();
            setupTrustStore();
            setupKeyStore();
            minaNetwork = new MinaNetwork(getKeyStore().getJsKeyStore(), getTrustStore().getJsKeyStore(), TEMP_KEYSTORE_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cleanupListener () {
        if (null != getLocalListener())
            getLocalListener().stop();
    }

    @After
    public void cleanup() {
        cleanupTrustStore();
        cleanupKeyStore();
        cleanupListener();
    }

    @Test
    public void testConstructor() {
        pause(500);
        assert (getMinaNetwork().getCurrentState() instanceof NetworkReadyState);
        pause(500);
    }

    @Test
    public void testBasicConnectTo() throws Exception {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        pause(500);

        setupMinaListener(6789, getKeyStore().getJsKeyStore(), getTrustStore().getJsKeyStore(), TEMP_KEYSTORE_PASSWORD);

        pause(500);

        Handle handle = getMinaNetwork().basicConnectTo("localhost", 6789);

        pause(1000);

        assert (handle != null);
    }

    @Test
    public void testCreateHandle() {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        setupMockMiranda();
        setupMockCluster();
        when(getMockCluster().getQueue()).thenReturn(queue);
        when(getMockMiranda().getCluster()).thenReturn(getMockCluster());

        Handle handle = getMinaNetwork().createHandle(getMockIoSession());

        assert (handle instanceof MinaHandle);

        pause(500);
    }
}
