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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Clark on 3/6/2017.
 */
public class MinaNetwork extends Network {
    private boolean useEncryption;
    private KeyStore keyStore;
    private KeyStore truststore;
    private String keyStorePassword;

    public boolean isUseEncryption() {
        return useEncryption;
    }

    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public KeyStore getTruststore() {
        return truststore;
    }

    public void setTruststore(KeyStore truststore) {
        this.truststore = truststore;
    }

    public void setKeyStore(KeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public MinaNetwork(KeyStore keyStore, KeyStore truststore, String keyStorePassword) throws MirandaException {
        this.keyStore = keyStore;
        this.truststore = truststore;
        this.keyStorePassword = keyStorePassword;
        setUseEncryption(true);
    }

    public MinaNetwork () throws MirandaException {
        setUseEncryption(false);
    }

    public Handle createHandle(Object o) {
        IoSession ioSession = (IoSession) o;
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();

        return new MinaHandle(ioSession, queue);
    }

    public Handle basicConnectTo(String host, int port) throws MirandaException {
        try {
            NioSocketConnector nioSocketConnector = new NioSocketConnector();

            if (isUseEncryption()) {
                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(getKeyStore(), getKeyStorePassword().toCharArray());

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(getTruststore());

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
                SslFilter sslFilter = new SslFilter(sslContext);
                sslFilter.setUseClientMode(true);
                nioSocketConnector.getFilterChain().addLast("tls", sslFilter);
            }

            InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);

            if (isUseEncryption()) {
                Certificate certificate = getTruststore().getCertificate("ca");

                nioSocketConnector.setHandler(new ConnectionHandler(this, certificate));
            } else {
                nioSocketConnector.setHandler(new UnencryptedConnectionHandler());
            }

            ConnectFuture connectFuture = nioSocketConnector.connect(inetSocketAddress);
            connectFuture.awaitUninterruptibly();

            LinkedBlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
            return new MinaHandle(connectFuture.getSession(), queue);
        } catch (GeneralSecurityException e) {
            throw new MirandaException("Exception trying to connect", e);
        }
    }


}
