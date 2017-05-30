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

package com.ltsllc.miranda.newMina;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.cluster.Cluster;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.bouncycastle.asn1.x509.Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Created by Clark on 5/30/2017.
 */
public class NewNetworkListener extends Consumer {
    public static final String NAME = "listener";
    private int port;
    private KeyStore keystore;
    private KeyStore truststore;
    private Cluster cluster;

    public Cluster getCluster() {
        return cluster;
    }

    public KeyStore getKeystore() {
        return keystore;
    }

    public KeyStore getTruststore() {
        return truststore;
    }

    public int getPort() {

        return port;
    }

    public NewNetworkListener(String name, int port, Cluster cluster, KeyStore keyStore, KeyStore truststore) {
        super(NAME);
        this.port = port;
        this.keystore = keyStore;
        this.truststore = truststore;
        this.cluster = cluster;
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

        nioSocketAcceptor.setHandler(new NewConnectionHandler(this));

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
