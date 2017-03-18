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
import jdk.nashorn.internal.ir.LexicalContextNode;
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

    private boolean testMode = true; // for testing
    private IoAcceptor acceptor; // for testing
    private String testMessage; // for testing
    private MinaTestHandler minaTestHandler; // for testing

    public MinaTestHandler getMinaTestHandler() {
        return minaTestHandler;
    }

    public void setMinaTestHandler(MinaTestHandler minaTestHandler) {
        this.minaTestHandler = minaTestHandler;
    }

    public String getTestMessage() {
        return testMessage;
    }

    public void setTestMessage(String testMessage) {
        this.testMessage = testMessage;

        if (null != testMessage && testMessage.length() > 0)
            setTestMode(true);
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public boolean getUseEncryption() {
        return useEncryption;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    public IoAcceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(IoAcceptor acceptor) {
        this.acceptor = acceptor;
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
        setAcceptor(acceptor);

        SSLContext sslContext = factory.buildServerSSLContext();

        if (null != sslContext) {
            SslFilter sslFilter = new SslFilter(sslContext);
            acceptor.getFilterChain().addLast("tls", sslFilter);
        }

        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
        acceptor.getFilterChain().addLast("lines", protocolCodecFilter);

        if (isTestMode()) {
            MinaTestHandler minaTestHandler = new MinaTestHandler(getTestMessage());
            setMinaTestHandler(minaTestHandler);
            acceptor.setHandler(minaTestHandler);
        } else {
            MinaIncomingHandler handler = new MinaIncomingHandler(queue);
            acceptor.setHandler(handler);
        }

        InetSocketAddress address = new InetSocketAddress(getPort());

        try {
            acceptor.bind(address);
        } catch (IOException e) {
            Panic panic = new StartupPanic("Exception trying to listen", e, StartupPanic.StartupReasons.ExceptionListening);
            Miranda.getInstance().panic(panic);
        }
    }
}
