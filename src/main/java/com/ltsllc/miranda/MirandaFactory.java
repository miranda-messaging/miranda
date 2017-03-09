package com.ltsllc.miranda;

import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.MirandaPanicPolicy;
import com.ltsllc.miranda.miranda.PanicPolicy;
import com.ltsllc.miranda.netty.NettyHttpServer;
import com.ltsllc.miranda.netty.NettyNetwork;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.server.HttpServer;
import com.ltsllc.miranda.servlet.PropertiesServlet;
import com.ltsllc.miranda.servlet.StatusServlet;
import com.ltsllc.miranda.socket.SocketHttpServer;
import com.ltsllc.miranda.socket.SocketNetwork;
import com.ltsllc.miranda.util.Utils;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletHandler;
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

    public Network buildNetwork () throws MirandaException {
        Network network = null;
        MirandaProperties.Networks networks = getProperties().getNetworkProperty();

        switch (networks) {
            case Netty: {
                network = new NettyNetwork(this);
                break;
            }

            case Socket: {
                network = new SocketNetwork();
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
        int port = getProperties().getIntProperty(MirandaProperties.PROPERTY_HTTP_PORT);
        String httpBase = getProperties().getProperty(MirandaProperties.PROPERTY_HTTP_BASE);
        HttpServer httpServer = null;

        switch (whichServer) {
            default: {
                httpServer = buildJetty(port, httpBase);
                break;
            }
        }

        return httpServer;
    }


    public HttpServer buildJetty (int port, String httpBase) throws MirandaException {
        try {
            MirandaProperties properties = Miranda.properties;

            //
            // jetty wants some properties defined
            //
            String base = properties.getProperty(MirandaProperties.PROPERTY_HTTP_BASE);

            File file = new File(httpBase);
            base = file.getCanonicalPath();

            properties.setProperty(JETTY_BASE, base);
            properties.setProperty(JETTY_HOME, base);
            properties.setProperty(JETTY_TAG, DEFAULT_JETTY_TAG);
            properties.updateSystemProperties();

            Server jetty = new Server();

            ResourceHandler resource_handler = new ResourceHandler();

            // Configure the ResourceHandler. Setting the resource base indicates where the files should be served out of.
            // In this example it is the current directory but it can be configured to anything that the jvm has access to.
            resource_handler.setDirectoriesListed(true);
            resource_handler.setWelcomeFiles(new String[]{ "index.html" });
            resource_handler.setResourceBase(base);

            // The ServletHandler is a dead simple way to create a context handler
            // that is backed by an instance of a Servlet.
            // This handler then needs to be registered with the Server object.
            ServletHandler servletHandler = new ServletHandler();

            // IMPORTANT:
            // This is a raw Servlet, not a Servlet that has been configured
            // through a web.xml @WebServlet annotation, or anything similar.
            servletHandler.addServletWithMapping(PropertiesServlet.class, "/admin/properties");
            servletHandler.addServletWithMapping(StatusServlet.class, "/admin/status");

            // Add the handlers to the server.
            HandlerList handlers = new HandlerList();
            // handlers.setHandlers(new Handler[] { servletHandler, resource_handler, new DefaultHandler()});
            handlers.setHandlers(new Handler[] { resource_handler, servletHandler, new DefaultHandler()});
            jetty.setHandler(handlers);

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
            sslConnector.setPort(443);

            ServerConnector connector = new ServerConnector(jetty);
            connector.setPort(80);

            jetty.setConnectors(new Connector[] { sslConnector });

            jetty.start();

            return new SocketHttpServer(jetty);
        } catch (Exception e) {
            throw new MirandaException("Exception trying to setup web server", e);
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

}
