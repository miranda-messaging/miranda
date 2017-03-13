package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.MirandaFactory;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/6/2017.
 */
public class MinaNetworkListener extends NetworkListener {
    private boolean useEncryption = true;

    public boolean getUseEncryption() {
        return useEncryption;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    public MinaNetworkListener (int port) {
        super(port);
    }

    public MinaNetworkListener (int port, boolean useEncryption)
    {
        this(port);
        setUseEncryption(useEncryption);
    }

    public void startup (BlockingQueue<Handle> queue) {
        MirandaFactory factory = Miranda.factory;

        IoAcceptor acceptor = new NioSocketAcceptor();

        SSLContext sslContext = factory.buildServerSSLContext();

        if (null != sslContext) {
            SslFilter sslFilter = new SslFilter(sslContext);
            acceptor.getFilterChain().addLast("tls", sslFilter);
        }

        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
        acceptor.getFilterChain().addLast("lines", protocolCodecFilter);

        MinaIncomingHandler handler = new MinaIncomingHandler(queue);
        acceptor.setHandler (handler);

        InetSocketAddress address = new InetSocketAddress(getPort());

        try {
            acceptor.bind(address);
        } catch (IOException e) {
            Panic panic = new StartupPanic("Exception trying to listen", e, StartupPanic.StartupReasons.ExceptionListening);
            Miranda.getInstance().panic(panic);
        }
    }
}
