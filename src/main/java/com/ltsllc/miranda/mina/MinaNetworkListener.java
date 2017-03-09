package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.MirandaFactory;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
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
    private static class LocalHandler extends IoHandlerAdapter {
        private BlockingQueue<Handle> queue;

        public LocalHandler (BlockingQueue<Handle> queue) {
            this.queue = queue;
        }

        @Override
        public void sessionCreated(IoSession session) throws Exception {
            Node node = new Node();

            MinaHandle handle = new MinaHandle(node.getQueue(), session);

            queue.put(handle);
        }
    }

    public void startup (BlockingQueue<Handle> queue) {
        MirandaFactory factory = Miranda.factory;
        MirandaProperties properties = Miranda.properties;

        IoAcceptor acceptor = new NioSocketAcceptor();

        SSLContext sslContext = factory.buildServerSSLContext();

        if (null != sslContext) {
            SslFilter sslFilter = new SslFilter(sslContext);
            acceptor.getFilterChain().addLast("tls", sslFilter);
        }

        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
        acceptor.getFilterChain().addLast("lines", protocolCodecFilter);

        LocalHandler handler = new LocalHandler(queue);
        acceptor.setHandler (handler);

        InetSocketAddress address = new InetSocketAddress(properties.getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT));

        try {
            acceptor.bind(address);
        } catch (IOException e) {
            Panic panic = new StartupPanic("Exception trying to listen", e, StartupPanic.StartupReasons.ExceptionListening);
            Miranda.getInstance().panic(panic);
        }
    }
}
