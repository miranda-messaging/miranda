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

package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.MirandaFactory;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.NetworkListenerHolder;
import com.ltsllc.miranda.network.messages.NewConnectionMessage;
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
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 5/23/2017.
 */
public class NewMinaNetworkListener {
    private NioSocketAcceptor acceptor;
    private int port;



    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public NioSocketAcceptor getAcceptor() {
        return acceptor;
    }

    public void setAcceptor(NioSocketAcceptor acceptor) {
        this.acceptor = acceptor;
    }


    public void listen() {
        MirandaFactory factory = Miranda.factory;

        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        setAcceptor(acceptor);

        SSLContext sslContext = factory.buildServerSSLContext();

        SslFilter sslFilter = new SslFilter(sslContext);
        // sslFilter.setNeedClientAuth(true);
        //acceptor.getFilterChain().addLast("tls", sslFilter);

        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        ProtocolCodecFilter protocolCodecFilter = new ProtocolCodecFilter(textLineCodecFactory);
        // acceptor.getFilterChain().addLast("lines", protocolCodecFilter);
        acceptor.setReuseAddress(true);
        NewMinaIncomingHandler newMinaIncomingHandler = new NewMinaIncomingHandler(this);
        acceptor.setHandler(newMinaIncomingHandler);

        InetSocketAddress address = new InetSocketAddress(getPort());

        try {
            acceptor.bind(address);
        } catch (IOException e) {
            Panic panic = new StartupPanic("Exception trying to bind", e, StartupPanic.StartupReasons.ExceptionListening);
            Miranda.getInstance().panic(panic);
        }

    }

    public void newConnection (IoSession ioSession) {
// test
    }
}
