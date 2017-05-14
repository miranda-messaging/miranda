package com.ltsllc.miranda.socket;

import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkListener;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/1/2017.
 */
public class SocketNetworkListener extends NetworkListener {
    private static Logger logger = Logger.getLogger(SocketNetworkListener.class);

    private ServerSocket serverSocket;
    private BlockingQueue<Handle> handleQueue;

    public SocketNetworkListener (int port) {
        super(port);
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public BlockingQueue<Handle> getHandleQueue() {
        return handleQueue;
    }

    private SSLContext createSslContext (String keystorePassword, String truststorePassword) throws GeneralSecurityException, IOException {
        MirandaProperties properties = Miranda.getInstance().properties;
        MirandaProperties.EncryptionModes encryptionMode = properties.getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        SSLContext sslContext = null;

        if (encryptionMode == MirandaProperties.EncryptionModes.LocalCA) {
            String trustStoreFileneame = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            KeyStore keyStore = Utils.loadKeyStore(trustStoreFileneame, truststorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            String serverKeyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_FILE);
            keyStore = Utils.loadKeyStore(serverKeyStoreFilename, keystorePassword);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
        }

        return sslContext;
    }

    public void startup(BlockingQueue<Handle> queue) {
        Panic panic = null;
        SSLContext sslContext = Miranda.factory.buildServerSSLContext();

        ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();
        if (null != sslContext) {
            serverSocketFactory = sslContext.getServerSocketFactory();
        }

        try {
            setServerSocket(serverSocketFactory.createServerSocket());
        } catch (IOException e) {
            String message = "Exception trying to create server socket";
            logger.error (message, e);
            panic = new StartupPanic(message, e, StartupPanic.StartupReasons.NetworkListener);
            Miranda.getInstance().panic(panic);
        }


        if (panic == null) {
            InetSocketAddress address = new InetSocketAddress(getPort());
            try {
                getServerSocket().bind(address);
            } catch (IOException e) {
                String message = "Exception during bind to port " + getPort();
                logger.error (message, e);
                panic = new StartupPanic(message, e, StartupPanic.StartupReasons.NetworkListener);
                Miranda.getInstance().panic(panic);
            }
        }

        getConnections();
    }

    public void getConnections () {
        while (keepGoing()) {
            try {
                Socket socket = getServerSocket().accept();
                SocketHandle handle = new SocketHandle(Network.getInstance().getQueue(), socket);
                getHandleQueue().put(handle);
            } catch (Exception e) {
                String message = "Exception while trying to get new connection";
                logger.error(message, e);
                Panic panic = new Panic (message, e, Panic.Reasons.ExceptionWaitingForNextConnection);
                if (Network.getInstance().panic(panic)) {
                    setKeepGoing(false);
                }
            }
        }
    }

    public void stopListening () {
        // TODO: Implement this
    }
}
