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

import com.ltsllc.clcl.JavaKeyStore;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.ConnectionListener;
import com.ltsllc.miranda.network.Network;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;

/**
 * Created by Clark on 3/6/2017.
 */
public class MinaNetworkListener extends ConnectionListener {
    public static final String NAME = "listener";

    @Override
    public void startListening() {

    }

    private static NioSocketAcceptor nioSocketAcceptor;

    private Network network;
    private JavaKeyStore keystore;
    private JavaKeyStore truststore;
    private String keyStorePassword;

    public static NioSocketAcceptor getNioSocketAcceptor() {
        return nioSocketAcceptor;
    }

    public static void setNioSocketAcceptor(NioSocketAcceptor nioSocketAcceptor) {
        MinaNetworkListener.nioSocketAcceptor = nioSocketAcceptor;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public Network getNetwork() {
        return network;
    }

    public JavaKeyStore getKeystore() {
        return keystore;
    }

    public JavaKeyStore getTruststore() {
        return truststore;
    }

    public MinaNetworkListener(int port, JavaKeyStore keyStore, String getKeyStorePassword, JavaKeyStore truststore,
                               Network network) throws MirandaException {
        super(port, null,null);
        this.network = network;
        this.keystore = keyStore;
        this.keyStorePassword = getKeyStorePassword;
        this.truststore = truststore;
    }

    public static void allStopListening() {
        if (null != getNioSocketAcceptor()) {
            getNioSocketAcceptor().unbind();
            setNioSocketAcceptor(null);
        }
    }

    public void stopListening() {
        stop();
    }

    public void stop() {
        getThread().interrupt();
    }

    public void basicStart() throws Exception {
        NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
        setNioSocketAcceptor(nioSocketAcceptor);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(getKeystore().getJsKeyStore(), getKeyStorePassword().toCharArray());

        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(getTruststore().getJsKeyStore());

        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        SslFilter sslFilter = new SslFilter(sslContext);
        sslFilter.setNeedClientAuth(true);
        nioSocketAcceptor.getFilterChain().addLast("ssl", sslFilter);

        Certificate certificate = getTruststore().getCertificate("ca").getCertificate();
        ConnectionHandler connectionHandler = new ConnectionHandler(getNetwork(), certificate);
        nioSocketAcceptor.setHandler(connectionHandler);

        InetSocketAddress inetSocketAddress = new InetSocketAddress(getPort());

        nioSocketAcceptor.setReuseAddress(true);

        nioSocketAcceptor.bind(inetSocketAddress);

    }

    public void start() {
        try {
            basicStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newConnection(IoSession session) {

    }

}
