package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.cluster.NewNodeMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.*;
import com.ltsllc.miranda.network.messages.ConnectToMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.util.Utils;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by Clark on 3/6/2017.
 */
public class MinaNetwork extends Network {

    public MinaNetwork () {
        NetworkReadyState readyState = new NetworkReadyState(this);
        setCurrentState(readyState);
    }

    public Handle basicConnectTo (ConnectToMessage connectToMessage) throws NetworkException
    {
        Handle handle = null;

        try {
            MirandaProperties properties = Miranda.properties;
            String filename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String password = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);

            KeyStore keyStore = Utils.loadKeyStore(filename, password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

            SslFilter sslFilter = new SslFilter(sslContext);

            sslFilter.setUseClientMode(true);

            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("tls",sslFilter);

            TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName( "UTF-8" ));
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
            connector.getFilterChain().addLast("line", protocolCodecFilter);

            MinaHandler minaHandler = new MinaHandler();

            connector.setHandler(minaHandler);

            InetSocketAddress address = new InetSocketAddress(connectToMessage.getHost(), connectToMessage.getPort());
            ConnectFuture connectFuture = connector.connect(address);

            handle = new MinaHandle(minaHandler, connectToMessage.getSender());

            FutureListener futureListener = new FutureListener(connectToMessage.getSender(), nextHandle(), this, handle);
            connectFuture.addListener(futureListener);
        } catch (GeneralSecurityException e) {
            throw new NetworkException("Exception trying to create mina network", e, NetworkException.Errors.ExceptionCreating);
        }

        return handle;
    }


    public MinaIncomingHandle newConnection (MinaIncomingHandler minaIncomingHandler) {
        int handle = nextHandle();
        Node node = new Node(handle, this, Cluster.getInstance());

        MinaIncomingHandle minaIncomingHandle = new MinaIncomingHandle(node.getQueue(), minaIncomingHandler);
        setHandle(handle, minaIncomingHandle);

        NewNodeMessage newNodeMessage = new NewNodeMessage(getQueue(), this, node);
        send(newNodeMessage, Cluster.getInstance().getQueue());

        return minaIncomingHandle;
    }
}
