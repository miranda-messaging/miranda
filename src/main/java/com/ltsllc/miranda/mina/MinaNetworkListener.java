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

import com.ltsllc.miranda.MirandaFactory;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.newMina.NewConnectionHandler;
import com.ltsllc.miranda.newMina.NewNetwork;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/6/2017.
 */
public class MinaNetworkListener extends NetworkListener {
    public static final String NAME = "listener";
    private Network network;
    private KeyStore keystore;
    private KeyStore truststore;

    public Network getNetwork() {
        return network;
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public KeyStore getTruststore() {
        return truststore;
    }

    public MinaNetworkListener(int port, KeyStore keyStore, KeyStore truststore) {
        super(port);
        this.keystore = keyStore;
        this.truststore = truststore;
    }

    public void stopListening () {
        stop();
    }

    public void stop () {
        getThread().interrupt();
    }

    public void basicStart () throws Exception {
        NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setNeedClientAuth(true);
        nioSocketAcceptor.getFilterChain().addLast("ssl", sslFilter);

        ConnectionHandler connectionHandler = new ConnectionHandler(getNetwork());
        nioSocketAcceptor.setHandler(connectionHandler);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(getPort());

        nioSocketAcceptor.bind(inetSocketAddress);
    }

    public void start () {
        try {
            basicStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newConnection (IoSession session) {

    }

}
