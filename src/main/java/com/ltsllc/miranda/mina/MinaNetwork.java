package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.ConnectToMessage;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkException;
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

            NioSocketConnector connector = new NioSocketConnector();
            connector.getFilterChain().addLast("tls",sslFilter);

            TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName( "UTF-8" ));
            ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
            connector.getFilterChain().addLast("line", protocolCodecFilter);

            InetSocketAddress address = new InetSocketAddress(connectToMessage.getHost(), connectToMessage.getPort());
            ConnectFuture future = connector.connect(address);
            handle = new MinaHandle(connectToMessage.getSender(), future, connector);
        } catch (GeneralSecurityException e) {
            throw new NetworkException("Exception trying to create mina network", e, NetworkException.Errors.ExceptionCreating);
        }

        return handle;
    }


}
