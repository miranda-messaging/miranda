package com.ltsllc.miranda;

import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.http.JettyHttpServer;
import com.ltsllc.miranda.mina.MinaNetwork;
import com.ltsllc.miranda.mina.MinaNetworkListener;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.miranda.PanicPolicy;
import com.ltsllc.miranda.netty.NettyNetworkListener;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.socket.SocketNetworkListener;
import com.ltsllc.miranda.util.Utils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.security.*;
import java.security.cert.X509Certificate;

/**
 * Based on the values of the properties, this class knows which classes to
 * build.
 */
public class MirandaFactory {
    private static Logger logger = Logger.getLogger(MirandaFactory.class);

    private MirandaProperties properties;

    public MirandaProperties getProperties() {
        return properties;
    }

    public MirandaFactory (MirandaProperties properties) {
        this.properties = properties;
    }

    public NetworkListener buildNetworkListener () {
        NetworkListener networkListener = null;
        MirandaProperties.Networks networks = getProperties().getNetworkProperty();
        int port = getProperties().getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);

        switch (networks) {
            case Mina: {
                networkListener = buildMinaNetworkListener();
                break;
            }

            case Netty: {
                networkListener = buildNettyNetworkListener();
                break;
            }

            case Socket: {
                networkListener = buildSocketNetworkListener();
                break;
            }

            default: {
                logger.fatal("Unrecognized network: " + networks);
                Panic panic = new StartupPanic("Unrecognized network", null, StartupPanic.StartupReasons.NetworkListener);
                Miranda.getInstance().panic(panic);
            }
        }

        return networkListener;
    }

    public Network buildNetwork () throws MirandaException {
        Network network = null;
        MirandaProperties.Networks networks = getProperties().getNetworkProperty();

        switch (networks) {
            case Netty: {
                network = buildNettyNetwork();
                break;
            }

            case Socket: {
                network = buildSocketNetwork();
                break;
            }

            case Mina: {
                network = buildMinaNetwork();
                break;
            }

            default: {
                throw new IllegalArgumentException("unknown network: " + networks);
            }
        }

        return network;
    }

    public SslContext buildNettyClientSslContext () throws SSLException {
        String filename = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String password = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);

        return Utils.createClientSslContext(filename, password);
    }

    public void checkProperty (String name, String value) throws MirandaException {
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

    public SslContext buildLocalCaServerSslContext () throws MirandaException {
        MirandaProperties properties = Miranda.properties;

        String serverKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE);
        checkProperty(MirandaProperties.PROPERTY_KEYSTORE, serverKeyStoreFilename);

        String serverKeyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
        checkProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD, serverKeyStorePassword);

        String serverKeyStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS);
        checkProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS, serverKeyStoreAlias);

        String trustStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        checkProperty(MirandaProperties.PROPERTY_TRUST_STORE, trustStoreFilename);

        String trustStorePassword = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
        checkProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD, trustStorePassword);

        String trustStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS);
        checkProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS, trustStoreAlias);

        return Utils.createServerSslContext(serverKeyStoreFilename, serverKeyStorePassword, serverKeyStoreAlias,
                trustStoreFilename, trustStorePassword, trustStoreAlias);
    }


    public SslContext buildRemoteCaServerContext () throws MirandaException {
        try {
            MirandaProperties properties = Miranda.properties;

            String serverKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE, serverKeyStoreFilename);

            String serverKeyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD, serverKeyStorePassword);

            String serverKeyStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS, serverKeyStoreAlias);

            String certificateKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_STORE);
            checkProperty(MirandaProperties.PROPERTY_CERTIFICATE_STORE, certificateKeyStoreFilename);

            String certificateKeyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_PASSWORD);
            checkProperty(MirandaProperties.PROPERTY_CERTIFICATE_PASSWORD, certificateKeyStorePassword);

            String certificateKeyStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);
            checkProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS, certificateKeyStoreAlias);

            PrivateKey key = Utils.loadKey(serverKeyStoreFilename, serverKeyStorePassword, serverKeyStoreAlias);
            X509Certificate certificate = Utils.loadCertificate(certificateKeyStoreFilename, certificateKeyStorePassword, certificateKeyStoreAlias);

            return SslContextBuilder
                    .forServer(key, certificate)
                    .build();
        } catch (SSLException e) {
            throw new MirandaException("Exception trying to create SSL context", e);
        }

    }

    private static final String JETTY_BASE = "jetty.base";
    private static final String JETTY_HOME = "jetty.home";
    private static final String JETTY_TAG = "jetty.tag.version";
    private static final String DEFAULT_JETTY_TAG = "master";


    public HttpServer buildHttpServer () throws MirandaException {
        MirandaProperties.WebSevers whichServer = getProperties().getHttpServerProperty(MirandaProperties.PROPERTY_HTTP_SERVER);
        int httpPort = getProperties().getIntProperty(MirandaProperties.PROPERTY_HTTP_PORT);
        int sslPort = getProperties().getIntProperty(MirandaProperties.PROPERTY_HTTP_SSL_PORT);
        String httpBase = getProperties().getProperty(MirandaProperties.PROPERTY_HTTP_BASE);

        HttpServer httpServer = null;

        switch (whichServer) {
            default: {
                httpServer = buildJetty(httpPort, sslPort, httpBase);
                break;
            }
        }

        return httpServer;
    }


    public HttpServer buildJetty (int httpPort, int sslPort, String httpBase) throws MirandaException {
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

            Server jetty = new Server();


            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[]{ "index.html" });
            resourceHandler.setResourceBase(base);

            HandlerCollection handlerCollection = new HandlerCollection();
            handlerCollection.addHandler(resourceHandler);

            jetty.setHandler(handlerCollection);

            String serverKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE, serverKeyStoreFilename);

            String serverKeyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD, serverKeyStorePassword);

            HttpConfiguration https = new HttpConfiguration();
            https.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(serverKeyStoreFilename);
            sslContextFactory.setKeyStorePassword(serverKeyStorePassword);
            sslContextFactory.setKeyManagerPassword(serverKeyStorePassword);

            ServerConnector sslConnector = new ServerConnector(jetty,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(https));
            sslConnector.setPort(sslPort);

            ServerConnector connector = new ServerConnector(jetty);
            connector.setPort(httpPort);

            jetty.setConnectors(new Connector[] { sslConnector, connector });

            HttpServer httpServer = new JettyHttpServer(jetty, handlerCollection);
            httpServer.start(); // this starts the HttpServer instance not jetty

            return httpServer;
        } catch (Exception e) {
            throw new MirandaException("Exception trying to setup http server", e);
        }
    }

    public SSLContext buildLocalCaSocketServerSslContext () throws MirandaException {
        SSLContext sslContext = null;

        try {
            MirandaProperties properties = Miranda.properties;

            String serverKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE, serverKeyStoreFilename);

            String serverKeyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD, serverKeyStorePassword);

            String serverKeyStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS);
            checkProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS, serverKeyStoreAlias);

            String trustStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            checkProperty(MirandaProperties.PROPERTY_TRUST_STORE, trustStoreFilename);

            String trustStorePassword = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            checkProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD, trustStorePassword);

            String trustStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS);
            checkProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS, trustStoreAlias);

            sslContext = Utils.createSocketServerSslContext(serverKeyStoreFilename, serverKeyStorePassword, serverKeyStoreAlias,
                    trustStoreFilename, trustStorePassword, trustStoreAlias);
        } catch (NoSuchAlgorithmException e) {
            throw new MirandaException("Exception trying to get SSL context", e);
        }

        return sslContext;
    }

    public SSLContext buildServerSSLContext () {
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
        }

        return sslContext;
    }

    public SSLContext buildRemoteCaServerSSLContext () {
        SSLContext sslContext = null;

        try {
            String keyStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            String keyStorePassword = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            KeyStore keyStore = Utils.loadKeyStore(keyStoreFilename, keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic ("Exception while trying to create SSL context", e,
                    Panic.Reasons.ExceptionCreatingSslContext);

            Miranda.getInstance().panic(panic);
        }

        return sslContext;
    }

    public SSLContext buildLocalCaServerSSLContext () {
        SSLContext sslContext = null;

        try {
            String trustStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String trustStorePassword = getProperties().getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            KeyStore trustKeyStore = Utils.loadKeyStore(trustStoreFilename, trustStorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustKeyStore);

            String keyStoreFilename = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            String keyStorePassword = getProperties().getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            KeyStore keyStore = Utils.loadKeyStore(keyStoreFilename, keyStorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic ("Exception while trying to create SSL context", e,
                    Panic.Reasons.ExceptionCreatingSslContext);

            Miranda.getInstance().panic(panic);
        }

        return sslContext;
    }

    public PanicPolicy buildPanicPolicy () {
        int maxPanics = getProperties().getIntProperty(MirandaProperties.PROPERTY_PANIC_LIMIT);
        long timeout = getProperties().getLongProperty(MirandaProperties.PROPERTY_PANIC_TIMEOUT);

        return new MirandaPanicPolicy(maxPanics, timeout, Miranda.getInstance(), Miranda.timer);
    }

    public Network buildNettyNetwork () {
        throw new IllegalStateException("not impelmented");
    }

    public Network buildSocketNetwork () {
        throw new IllegalStateException("not impelmented");
    }

    public Network buildMinaNetwork () {
        Network network = null;
        MirandaProperties.EncryptionModes mode = getProperties().getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);

        switch (mode) {
            case LocalCA: {
                network = new MinaNetwork();
                break;
            }

            case None: {
                network = new MinaNetwork(false);
                break;
            }

            default: {
                Panic panic = new StartupPanic("Unrecogized encrypion mode: " + mode, null, StartupPanic.StartupReasons.UnrecognizedEncryptionMode);
                Miranda.getInstance().panic(panic);
            }
        }
        return network;
    }

    public NetworkListener buildNettyNetworkListener () {
        throw new IllegalStateException("not impelmented");
    }

    public NetworkListener buildSocketNetworkListener () {
        throw new IllegalStateException("not impelmented");
    }

    public NetworkListener buildMinaNetworkListener () {
        NetworkListener networkListener = null;
        MirandaProperties.EncryptionModes mode = getProperties().getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        int port = getProperties().getIntProperty(MirandaProperties.PROPERTY_CLUSTER_PORT);

        switch (mode) {
            case LocalCA: {
                networkListener = new MinaNetworkListener(port);
                break;
            }

            case None: {
                networkListener = new MinaNetworkListener(port, false);
                break;
            }

            default: {
                Panic panic = new StartupPanic("Unregognized encryption mode: " + mode, null, StartupPanic.StartupReasons.UnrecognizedEncryptionMode);
                Miranda.getInstance().panic(panic);
            }
        }

        return networkListener;
    }
}
