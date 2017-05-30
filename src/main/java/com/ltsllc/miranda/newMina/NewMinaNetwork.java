package com.ltsllc.miranda.newMina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.KeyStoreFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public class NewMinaNetwork extends NewNetwork {
    private KeyStore keyStore;
    private KeyStore truststore;

    public KeyStore getTruststore() {
        return truststore;
    }

    public KeyStore getKeyStore() {

        return keyStore;
    }

    public Handle createHandle (Object o) {
        IoSession ioSession = (IoSession) o;
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        return new NewMinaHandle(ioSession, queue);
    }

    public Handle basicConnectTo (String host, int port) throws GeneralSecurityException {
        NioSocketConnector nioSocketConnector = new NioSocketConnector();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setUseClientMode(true);
        nioSocketConnector.getFilterChain().addLast("tls", sslFilter);

        nioSocketConnector.setHandler(new NewConnectionHandler());
        ConnectFuture connectFuture = nioSocketConnector.connect();
        connectFuture.awaitUninterruptibly();

        LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        return new NewMinaHandle(connectFuture.getSession(), queue);
    }

}
