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

package last;

import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.mina.MinaNetworkListener;
import com.ltsllc.miranda.test.TestCase;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.junit.Before;
import org.mockito.Mock;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;

import static org.mockito.Mockito.mock;

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

    @Mock
    private SSLContext mockSslContext;

    @Mock
    private SSLServerSocketFactory mockSslServerSocketFactory;

    private MinaNetworkListener minaNetworkListener;
    private ClientHandler clientHandler;

    public SSLServerSocketFactory getMockSslServerSocketFactory() {
        return mockSslServerSocketFactory;
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public SSLContext getMockSslContext() {
        return mockSslContext;
    }

    public MinaNetworkListener getMinaNetworkListener() {
        return minaNetworkListener;
    }

    public void reset() throws MirandaException {
        super.reset();

        mockSslServerSocketFactory = null;
        mockSslContext = null;
        minaNetworkListener = null;
    }

    @Before
    public void setup() {
        try {
            reset();

            super.setup();

            setupMockProperties();
            setupMiranda();
            setuplog4j();
            setupTrustStore();
            setupKeyStore();

            mockSslServerSocketFactory = mock(SSLServerSocketFactory.class);
            mockSslContext = mock(SSLContext.class);
            minaNetworkListener = new MinaNetworkListener(6789, getKeyStore(), TEMP_KEYSTORE_PASSWORD, getTrustStore());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            System.out.println ("got connection, seding test message");

            IoSession session = connectFuture.getSession();
            session.write(TEST_MESSAGE);

            System.out.println("Sent test message");

            pause(250);

            CloseFuture closeFuture = session.closeNow();
            closeFuture.awaitUninterruptibly();
            connector.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


}
