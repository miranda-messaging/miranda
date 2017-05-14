package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.NetworkReadyState;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.network.messages.NetworkErrorMessage;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
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
    public static class ServerHandler extends IoHandlerAdapter {
        @Override
        public void sessionCreated(IoSession session) throws Exception {
            System.out.println ("Got connection");
        }

        @Override
        public void messageReceived(IoSession session, Object message ) throws Exception {
            String s = message.toString();
            s.trim();
            System.out.println("Got " + s);
            session.write(s);
        }
    }

    @Mock
    private MinaIncomingHandler mockMinaIncomingHadeler;

    private MinaNetwork minaNetwork;

    public MinaIncomingHandler getMockMinaIncomingHadeler() {
        return mockMinaIncomingHadeler;
    }

    public void reset() {
        super.reset();

        mockMinaIncomingHadeler = null;
        minaNetwork = null;
    }


    public MinaNetwork getMinaNetwork() {
        return minaNetwork;
    }

    public void setupMinaListener(int port) {
        try {
            String trustStoreFilename = TEMP_TRUSTSTORE;
            String trustStorePassword = TEMP_TRUSTSTORE_PASSWORD;

            String keyStoreFilename = TEMP_KEYSTORE;
            String keyStorePassword = TEMP_KEYSTORE_PASSWORD;

            KeyStore keyStore = Utils.loadKeyStore(keyStoreFilename, keyStorePassword);

            KeyStore trustStore = Utils.loadKeyStore(trustStoreFilename, trustStorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());

            IoAcceptor acceptor = new NioSocketAcceptor();

            SslFilter sslFilter = new SslFilter(sslContext);
            acceptor.getFilterChain().addLast("tls", sslFilter);


            acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

            IoHandler serverHandler = new ServerHandler();
            acceptor.setHandler(serverHandler);

            InetSocketAddress socketAddress = new InetSocketAddress(port);

            System.out.println("listening on port " + port);

            acceptor.bind(socketAddress);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static final String TEST_KEYSTORE_PASSWORD = "hi there";
    public static final String TEST_TRUSTSTORE_PASSWORD = "hi there";

    @Before
    public void setup() {
        reset();

        super.setup();

        setuplog4j();

        mockMinaIncomingHadeler = mock(MinaIncomingHandler.class);
        minaNetwork = new MinaNetwork(TEST_KEYSTORE_PASSWORD, TEST_TRUSTSTORE_PASSWORD);
    }

    @After
    public void cleanup () {
        cleanupTrustStore();
        cleanupKeyStore();
    }

    @Test
    public void testConstructor() {
        assert (getMinaNetwork().getCurrentState() instanceof NetworkReadyState);
    }

    @Test
    public void testCreateSslFilter() {
        setupMirandaProperties();
        setupTrustStore();
        setupKeyStore();

        SslFilter sslFilter = null;

        try {
            sslFilter = getMinaNetwork().createSslFilter();
        } catch (NetworkException e) {
            e.printStackTrace();
        }

        assert (sslFilter != null);
        assert (sslFilter != null && sslFilter.isUseClientMode());
    }

    @Test
    public void testBasicConnectTo() {
        setupMirandaProperties();
        setupTrustStore();
        setupKeyStore();
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        setupMirandaProperties();
        setupTrustStore();

        setupMinaListener(6789);

        ConnectToMessage connectToMessage = new ConnectToMessage("localhost", 6789, queue, this);

        Handle handle = null;

        try {
            handle = getMinaNetwork().basicConnectTo(connectToMessage);
        } catch (NetworkException e) {
            e.printStackTrace();
        }

        assert (getMinaNetwork().getConnector().getFilterChain().contains("line"));
        assert (getMinaNetwork().getConnector().getFilterChain().contains("tls"));

        pause(250);

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
    public void testCreateHandle () {
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        setupMockCluster();
        when(getMockCluster().getQueue()).thenReturn(queue);

        Handle handle = getMinaNetwork().createHandle(getMockMinaIncomingHadeler());

        verify(getMockCluster(), atLeastOnce()).getQueue();
        assert (contains(Message.Subjects.NewNode, queue));
        assert (getMinaNetwork().getNode() != null);
    }
}
