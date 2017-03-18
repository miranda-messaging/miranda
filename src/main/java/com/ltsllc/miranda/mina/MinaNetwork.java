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
import org.eclipse.jetty.server.Connector;

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
    private boolean useEncryption = true;

    private NioSocketConnector connector; // for testing
    private FutureListener futureListener; // for testing
    private Node node; // for testing

    public boolean getUseEncryption() {
        return useEncryption;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    public NioSocketConnector getConnector() {
        return connector;
    }

    public void setConnector(NioSocketConnector connector) {
        this.connector = connector;
    }

    public FutureListener getFutureListener() {
        return futureListener;
    }

    public void setFutureListener(FutureListener futureListener) {
        this.futureListener = futureListener;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public MinaNetwork () {
        NetworkReadyState readyState = new NetworkReadyState(this);
        setCurrentState(readyState);
        setInstance(this);
    }

    public MinaNetwork (boolean useEncryption) {
        this();
        setUseEncryption(useEncryption);
    }

    public SslFilter createSslFilter () throws NetworkException {
        if (!getUseEncryption())
            return null;

        SslFilter sslFilter = null;

        try {
            MirandaProperties properties = Miranda.properties;

            String filename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String password = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);

            KeyStore keyStore = Utils.loadKeyStore(filename, password);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            sslFilter = new SslFilter(sslContext);

            sslFilter.setUseClientMode(true);
        } catch (GeneralSecurityException e) {
            throw new NetworkException("Exception trying to create SslFilter", e, NetworkException.Errors.ExceptionConnecting);
        }

        return sslFilter;
    }


    public Handle basicConnectTo (ConnectToMessage connectToMessage) throws NetworkException
    {
        MinaHandle handle = null;
        MirandaProperties properties = Miranda.properties;

        NioSocketConnector connector = new NioSocketConnector();

        SslFilter sslFilter = createSslFilter();

        if (null != sslFilter) {
            connector.getFilterChain().addLast("tls",sslFilter);
        }

        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName( "UTF-8" ));
        ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
        connector.getFilterChain().addLast("line", protocolCodecFilter);

        MinaHandler minaHandler = new MinaHandler();

        connector.setHandler(minaHandler);

        InetSocketAddress address = new InetSocketAddress(connectToMessage.getHost(), connectToMessage.getPort());
        ConnectFuture connectFuture = connector.connect(address);

        handle = new MinaHandle(minaHandler, connectToMessage.getSender());
        minaHandler.setMinaHandle(handle);

        FutureListener futureListener = new FutureListener(connectToMessage.getSender(), nextHandle(), this, handle);
        connectFuture.addListener(futureListener);

        setFutureListener(futureListener);
        setConnector(connector);

        return handle;
    }


    public Handle createHandle (Object o) {
        MinaIncomingHandler minaIncomingHandler = (MinaIncomingHandler) o;
        int handle = nextHandle();
        Node node = new Node(handle, this, Cluster.getInstance());
        setNode(node);

        MinaIncomingHandle minaIncomingHandle = new MinaIncomingHandle(node.getQueue(), minaIncomingHandler);
        setHandle(handle, minaIncomingHandle);

        NewNodeMessage newNodeMessage = new NewNodeMessage(getQueue(), this, node);
        send(newNodeMessage, Cluster.getInstance().getQueue());

        return minaIncomingHandle;
    }
}
