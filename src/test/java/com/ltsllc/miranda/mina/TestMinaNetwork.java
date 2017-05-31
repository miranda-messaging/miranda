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
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.NetworkReadyState;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.servlet.holder.TestLoginHolder;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
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

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/18/2017.
 */
public class TestMinaNetwork extends TestCase {
    public static class LocalHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            super.sessionCreated(session);

            System.out.println ("Session created");
        }
    }

    public static class LocalListener implements Runnable {
        private Thread thread;

        public Thread getThread() {
            return thread;
        }

        public LocalListener () {
            thread = new Thread(this);
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

        public SslFilter createSslFilter () throws Exception {
            createFile(TEMP_KEYSTORE, TEMP_KEY_STORE_CONTENTS);
            KeyStore keyStore = Utils.loadKeyStore(TEMP_KEYSTORE, TEMP_KEYSTORE_PASSWORD);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, TEMP_KEYSTORE_PASSWORD.toCharArray());

            createFile(TEMP_TRUSTSTORE, TRUST_STORE_CONTENTS);
            KeyStore trustStore = Utils.loadKeyStore(TEMP_TRUSTSTORE, TEMP_TRUSTSTORE_PASSWORD);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(),
                    new SecureRandom());

            SslFilter sslFilter = new SslFilter(sslContext);

            return sslFilter;
        }


        public NioSocketAcceptor createAcceptor () throws Exception {
            NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
            nioSocketAcceptor.getFilterChain().addLast("tls", createSslFilter());

            LocalHandler handler = new LocalHandler();
            nioSocketAcceptor.setHandler(handler);

            return nioSocketAcceptor;
        }

        public void basicRun () throws Exception {
            NioSocketAcceptor nioSocketAcceptor = createAcceptor();

            InetSocketAddress inetSocketAddress = new InetSocketAddress(6789);
            nioSocketAcceptor.bind(inetSocketAddress);
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

    public MinaIncomingHandler getMockMinaIncomingHadeler() {
        return mockMinaIncomingHadeler;
    }

    public KeyStore getMockTrustStore() {
        return mockTrustStore;
    }

    public KeyStore getMockKeyStore() {

        return mockKeyStore;
    }

    public void reset() {
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

    public void setupMinaListener(int port, KeyStore keyStore, KeyStore trustStore) throws Exception {
        LocalListener listener = new LocalListener();
        listener.start();


    }

    public static final String TEST_KEYSTORE_PASSWORD = "hi there";
    public static final String TEST_TRUSTSTORE_PASSWORD = "hi there";

    @Before
    public void setup() {
        try {

            reset();

            super.setup();

            setuplog4j();

            setupKeyStore();
            setupTrustStore();
            setupMirandaProperties();
            minaNetwork = new MinaNetwork(getKeyStore(), getTrustStore());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanup() {
        cleanupTrustStore();
        cleanupKeyStore();
    }

    @Test
    public void testConstructor() {
        assert (getMinaNetwork().getCurrentState() instanceof NetworkReadyState);
    }

    @Test
    public void testBasicConnectTo() throws Exception {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        setupMinaListener(6789, getKeyStore(), getTrustStore());

        Handle handle = getMinaNetwork().basicConnectTo("localhost", 6789);

        pause(1000);

        assert (contains(Message.Subjects.ConnectSucceeded, queue));

        int theHandle = getMinaNetwork().getHandleCount();
        WireMessage joinWireMessage = new JoinWireMessage("foo.com", "192.168.1.1", 6789, "a node");
        SendNetworkMessage sendNetworkMessage = new SendNetworkMessage(queue, this, joinWireMessage, theHandle);

        try {
            if (null != handle)
                handle.send(sendNetworkMessage);
        } catch (NetworkException e) {
            e.printStackTrace();
        }

        pause(250);

        assert (containsNetworkMessage(joinWireMessage, queue));
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
    }
}
