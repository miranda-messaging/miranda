package com.ltsllc.miranda.socket;

import com.ltsllc.miranda.network.ConnectToMessage;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.log4j.Logger;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.net.Socket;
import java.security.SecureRandom;

/**
 * A "no netty" version of the network.
 * <p>
 * <p>
 * Given that nio TLS is so difficult, the no netty version uses a
 * separate thread for each connection.  This is a waste, but there
 * isn't an alternative that involves keeping your sanity.
 * </p>
 */
public class SocketNetwork extends Network {
    private Logger logger = Logger.getLogger(SocketNetwork.class);

    public SocketNetwork() {
        super();
    }

    public Handle basicConnectTo(ConnectToMessage connectToMessage) throws NetworkException {
        try {
            logger.info("Connecting to " + connectToMessage.getHost() + ":" + connectToMessage.getPort());

            MirandaProperties properties = Miranda.properties;
            SocketFactory socketFactory = SocketFactory.getDefault();

            if (properties.getEncryptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE) == MirandaProperties.EncryptionModes.LocalCA) {
                String trustStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
                String trustStorePassword = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
                TrustManagerFactory trustManagerFactory = Utils.createTrustManagerFactory(trustStoreFilename, trustStorePassword);

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());

                socketFactory = sslContext.getSocketFactory();
            }

            Socket socket = socketFactory.createSocket(connectToMessage.getHost(), connectToMessage.getPort());
            SocketHandle socketHandle = new SocketHandle(getQueue(), socket);

            return socketHandle;
        } catch (Exception e) {
            logger.warn ("Exception while trying to connect to " + connectToMessage.getHost() + ":" + connectToMessage.getPort(), e);
            throw new NetworkException(
                    "Exception while trying to connect to " + connectToMessage.getHost() + ":" + connectToMessage.getPort(),
                    e,
                    NetworkException.Errors.ExceptionConnecting);
        }
    }
}