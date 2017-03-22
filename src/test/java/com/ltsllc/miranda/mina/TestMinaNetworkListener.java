package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.node.networkMessages.JoinWireMessage;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.Utils;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 3/18/2017.
 */
public class TestMinaNetworkListener extends TestCase {
    public static class ClientHandler extends IoHandlerAdapter {
        private boolean gotMessage;

        public ClientHandler () {
            setGotMessage(false);
        }

        public boolean getGotMessage() {
            return gotMessage;
        }

        public void setGotMessage(boolean gotMessage) {
            this.gotMessage = gotMessage;
        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            System.out.println ("\nGot " + message.toString());

            if (message.toString().equals(TEST_MESSAGE))
                setGotMessage(true);
        }
    }

    private MinaNetworkListener minaNetworkListener;
    private ClientHandler clientHandler;

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public MinaNetworkListener getMinaNetworkListener() {
        return minaNetworkListener;
    }

    public void reset() {
        super.reset();

        minaNetworkListener = null;
    }

    @Before
    public void setup() {
        reset();

        setuplog4j();
        super.setup();
        setupMirandaFactory();
        setupTrustStore();
        setupKeyStore();

        minaNetworkListener = new MinaNetworkListener(6789);
    }

    public static final String TEST_MESSAGE = "hi there";

    public void setupMinaClient(String host, int port) {
        try {
            String trustStoreFilename = TEMP_TRUSTSTORE;
            String trustStorePassword = TEMP_TRUSTSTORE_PASSWORD;

            KeyStore keyStore = Utils.loadKeyStore(trustStoreFilename, trustStorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            NioSocketConnector connector = new NioSocketConnector();

            SslFilter sslFilter = new SslFilter(sslContext);
            sslFilter.setUseClientMode(true);
            connector.getFilterChain().addLast("tls", sslFilter);

            connector.getFilterChain().addLast("lines", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

            ClientHandler clientHandler = new ClientHandler();
            connector.setHandler(clientHandler);
            setClientHandler(clientHandler);

            System.out.println("connecting to " + host + ":" + port);

            InetSocketAddress address = new InetSocketAddress(host, port);
            ConnectFuture connectFuture = connector.connect(address);
            connectFuture.awaitUninterruptibly();

            IoSession session = connectFuture.getSession();
            session.write(TEST_MESSAGE);

            pause(250);

            CloseFuture closeFuture = session.closeNow();
            closeFuture.awaitUninterruptibly();
            connector.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Test the ability of the MinaNetworkListener to accept ne connections.
     *
     */
    @Test
    public void testStartup() {
        setuplog4j();
        setupMockMiranda();
        setupMockPanicPolicy();
        BlockingQueue<Handle> handleQueue = new LinkedBlockingQueue<Handle>();

        getMinaNetworkListener().setTestMessage(TEST_MESSAGE);
        getMinaNetworkListener().startup(handleQueue);

        setupMinaClient("localhost", 6789);

        pause(250);

        assert (getMinaNetworkListener().getAcceptor().getFilterChain().contains("tls"));
        assert (getMinaNetworkListener().getAcceptor().getFilterChain().contains("lines"));
        assert (getMinaNetworkListener().isTestMode() && getMinaNetworkListener().getMinaTestHandler().isSuccessfulTest());
    }
}
