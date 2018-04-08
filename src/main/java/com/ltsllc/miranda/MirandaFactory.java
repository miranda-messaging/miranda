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

package com.ltsllc.miranda;

import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.clcl.JavaKeyStore;
import com.ltsllc.commons.util.Utils;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.http.JettyHttpServer;
import com.ltsllc.miranda.mina.MinaNetwork;
import com.ltsllc.miranda.mina.MinaNetworkListener;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.miranda.PanicPolicy;
import com.ltsllc.miranda.network.ConnectionListener;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.property.MirandaProperties;
import io.netty.handler.ssl.SslContext;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;

/**
 * Based on the values of the properties, this class knows which classes to
 * build.
 */
public class MirandaFactory {
    private static Logger logger = Logger.getLogger(MirandaFactory.class);

    private MirandaProperties properties;
    private String keystorePassword;
    private String truststorePassword;
    private JavaKeyStore keyStore;
    private JavaKeyStore trustStore;

    public JavaKeyStore getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(JavaKeyStore trustStore) {
        this.trustStore = trustStore;
    }

    public JavaKeyStore getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(JavaKeyStore keyStore) {
        this.keyStore = keyStore;
    }

    public String getTruststorePassword() {
        return truststorePassword;
    }

    public String getKeystorePassword() {
        return keystorePassword;
    }

    public MirandaProperties getProperties() {
        return properties;
    }

    public MirandaFactory(MirandaProperties properties, String keystorePassword, String truststorePassword) {
        this.properties = properties;
        this.keystorePassword = keystorePassword;
        this.truststorePassword = truststorePassword;
    }


    public ConnectionListener buildNetworkListener(JavaKeyStore keyStore, JavaKeyStore trustStore) throws MirandaException {
        int port = getProperties().getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        return new MinaNetworkListener(port, keyStore, getKeystorePassword(), trustStore);
    }

    public Network buildNetwork(KeyStore keyStore, KeyStore trustStore) throws MirandaException {
        return new MinaNetwork(keyStore, trustStore, getKeystorePassword());
    }

    public void checkProperty(String name, String value) throws MirandaException {
        if (null == value || value.equals("")) {
            String message = "No or empty value for property " + name;
            logger.error(message);
            throw new MirandaException(message);
        }
    }

    public SslContext buildServerSslContext() throws MirandaException {
        MirandaProperties properties = Miranda.properties;
        MirandaProperties.EncryptionModes encryptionMode = properties.getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        SslContext sslContext = null;

        switch (encryptionMode) {
            case LocalCA: {
                sslContext = buildLocalCaServerSslContext();
                break;
            }

            case RemoteCA: {
                sslContext = buildRemoteCaServerContext();
                break;
            }
        }

        return sslContext;
    }

    public SslContext buildLocalCaServerSslContext() throws MirandaException {
        return null;
    }


    public SslContext buildRemoteCaServerContext() throws MirandaException {
        return null;
    }

    private static final String JETTY_BASE = "jetty.base";
    private static final String JETTY_HOME = "jetty.home";
    private static final String JETTY_TAG = "jetty.tag.version";
    private static final String DEFAULT_JETTY_TAG = "master";

    public HttpServer buildServletContainer() throws MirandaException {
        MirandaProperties.WebSevers whichServer = getProperties().getHttpServerProperty(MirandaProperties.PROPERTY_HTTP_SERVER);
        int sslPort = getProperties().getIntProperty(MirandaProperties.PROPERTY_HTTP_SSL_PORT);
        String httpBase = getProperties().getProperty(MirandaProperties.PROPERTY_HTTP_BASE);

        HttpServer httpServer = null;

        switch (whichServer) {
            default: {
                httpServer = buildJetty(sslPort, httpBase);
                break;
            }
        }

        return httpServer;
    }


    public HttpServer buildJetty(int sslPort, String httpBase) throws MirandaException {
        try {
            MirandaProperties properties = Miranda.properties;

            //
            // jetty wants some properties defined
            //
            File file = new File(httpBase);
            String base = file.getCanonicalPath();

            properties.setProperty(JETTY_BASE, base);
            properties.setProperty(JETTY_HOME, base);
            properties.setProperty(JETTY_TAG, DEFAULT_JETTY_TAG);
            properties.updateSystemProperties();

            int threadPoolSize = getProperties().getIntProperty(MirandaProperties.PROPERTY_SERVLET_THREAD_POOL_SIZE);
            QueuedThreadPool threadPool = new QueuedThreadPool(threadPoolSize);

            Server jetty = new Server(threadPool);

            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[]{"index.html"});
            resourceHandler.setResourceBase(base);

            HandlerCollection handlerCollection = new HandlerCollection(true);
            handlerCollection.addHandler(resourceHandler);

            jetty.setHandler(handlerCollection);

            String serverKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE, serverKeyStoreFilename);

            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(serverKeyStoreFilename);
            sslContextFactory.setKeyStorePassword(getKeystorePassword());
            sslContextFactory.setKeyManagerPassword(getKeystorePassword());

            ServerConnector sslConnector = new ServerConnector(jetty,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https));
            sslConnector.setPort(sslPort);
            jetty.setConnectors(new Connector[]{sslConnector});

            HttpServer httpServer = new JettyHttpServer(jetty, handlerCollection);
            httpServer.start(); // this starts the HttpServer instance not jetty

            return httpServer;
        } catch (Exception e) {
            throw new MirandaException("Exception trying to setup http server", e);
        }
    }

    public SSLContext buildServerSSLContext() {
        MirandaProperties.EncryptionModes mode = getProperties().getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        SSLContext sslContext = null;

        switch (mode) {
            case LocalCA: {
                sslContext = buildLocalCaServerSSLContext();
                break;
            }

            case RemoteCA: {
                sslContext = buildRemoteCaServerSSLContext();
                break;
            }

            default: {
                StartupPanic startupPanic = new StartupPanic("Unrecognized encryption mode: " + mode,
                        StartupPanic.StartupReasons.UnrecognizedEncryptionMode);
                Miranda.panicMiranda(startupPanic);
                break;
            }
        }

        return sslContext;
    }

    public SSLContext buildRemoteCaServerSSLContext() {
        SSLContext sslContext = null;

        try {
            String keyStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);
            JavaKeyStore javaKeyStore = new JavaKeyStore(keyStoreFilename, getKeystorePassword());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore.getJsKeyStore(), getKeystorePassword().toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
        } catch (GeneralSecurityException | EncryptionException | IOException e) {
            Panic panic = new Panic("Exception while trying to create SSL context", e,
                    Panic.Reasons.ExceptionCreatingSslContext);

            Miranda.getInstance().panic(panic);
        }

        return sslContext;
    }

    public SSLContext buildLocalCaServerSSLContext() {
        SSLContext sslContext = null;

        try {
            String trustStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE_FILENAME);
            KeyStore keyStore = JavaKeyStore.loadJsKeyStore(trustStoreFilename, getTruststorePassword());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            String keyStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);
            KeyStore keyStore2 = JavaKeyStore.loadJsKeyStore(keyStoreFilename, getKeystorePassword());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore2, getKeystorePassword().toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (GeneralSecurityException | EncryptionException e) {
            Panic panic = new Panic("Exception while trying to create SSL context", e,
                    Panic.Reasons.ExceptionCreatingSslContext);

            Miranda.getInstance().panic(panic);
        }

        return sslContext;
    }

    public PanicPolicy buildPanicPolicy() {
        int maxPanics = getProperties().getIntProperty(MirandaProperties.PROPERTY_PANIC_LIMIT);
        long timeout = getProperties().getLongProperty(MirandaProperties.PROPERTY_PANIC_TIMEOUT, MirandaProperties.DEFAULT_PANIC_TIMEOUT);

        return new MirandaPanicPolicy(maxPanics, timeout, Miranda.getInstance(), Miranda.timer);
    }

    public Network buildNettyNetwork() {
        throw new IllegalStateException("not impelmented");
    }

    public Network buildSocketNetwork() {
        throw new IllegalStateException("not impelmented");
    }

    public ConnectionListener buildNettyNetworkListener() {
        throw new IllegalStateException("not impelmented");
    }

    public ConnectionListener buildSocketNetworkListener() {
        throw new IllegalStateException("not impelmented");
    }

    public ConnectionListener buildNewNetworkListener() throws MirandaException {
        int port = getProperties().getIntegerProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);
        MirandaProperties.EncryptionModes mode = getProperties().getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        return new MinaNetworkListener(port, getKeyStore(), getKeystorePassword(), getTrustStore());
    }
}
