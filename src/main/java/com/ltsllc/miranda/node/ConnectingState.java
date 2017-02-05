package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.timer.ScheduleMessage;
import com.ltsllc.miranda.util.IOUtils;
import com.ltsllc.miranda.util.PropertiesUtils;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/21/2017.
 */
public class ConnectingState extends NodeState {
    private Logger logger = Logger.getLogger(ConnectingState.class);

    private Node node;

    public ConnectingState (Node node) {
        super(node);
    }


    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case Connected: {
                ConnectedMessage connectedMessage = (ConnectedMessage) m;
                nextState = processConnectedMessage(connectedMessage);
                break;
            }

            case ConnectionError: {
                ConnectFailedMessage connectFailedMessage = (ConnectFailedMessage) m;
                nextState = processConnectFailed(connectFailedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }

        }

        return nextState;
    }


    private State processConnectedMessage (ConnectedMessage connectedMessage) {
        logger.info("got connection");

        getNode().setChannel(connectedMessage.getChannel());
        ByteBufAllocator byteBufAllocator = connectedMessage.getChannel().alloc();

        connectedMessage.getChannel().pipeline().addLast(createSslEngine(byteBufAllocator));

        NodeHandler nodeHandler = new NodeHandler (getNode());
        connectedMessage.getChannel().pipeline().addLast(nodeHandler);

        JoinMessage joinMessage = new JoinMessage(getNode().getQueue());
        sendOnWire(joinMessage);

        return new JoiningState(getNode());
    }


    private SslHandler createSslEngine (ByteBufAllocator byteBufAllocator) {
        String trustStoreFilename = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
        String trustStorePassword = System.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);


        SslContext sslContext = null;

        try {
            KeyStore keyStore = getKeyStore(trustStoreFilename, trustStorePassword);
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext defaultContext = SSLContext.getDefault();
            SSLSocketFactory sslSocketFactory = defaultContext.getSocketFactory();
            String[] cipherSuites = sslSocketFactory.getDefaultCipherSuites();
            List<String> ciphers = Arrays.asList(cipherSuites);

            sslContext = SslContextBuilder
                    .forClient()
                    .ciphers(ciphers)
                    .trustManager(trustManagerFactory)
                    .build();
        } catch (Exception e) {
            logger.fatal ("Exception while trying to create SSL context", e);
            System.exit(1);
        }

        return sslContext.newHandler(byteBufAllocator);
    }


    private KeyStore getKeyStore (String filename, String passwordString) {
        FileInputStream fis = null;
        KeyStore keyStore = null;

        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            char[] password = null;
            if (null != passwordString)
                password = passwordString.toCharArray();

            fis = new FileInputStream(filename);
            keyStore.load(fis, password);
        }  catch (Exception e) {
            logger.fatal ("Exception trying to get truststore", e);
            System.exit(1);
        } finally {
            IOUtils.closeNoExceptions(fis);
        }

        return keyStore;
    }

    private State processConnectFailed (ConnectFailedMessage connectFailedMessage) {
        logger.info("Failed to get connection", connectFailedMessage.getCause());

        MirandaProperties p = MirandaProperties.getInstance();
        int delayBetweenRetries = PropertiesUtils.getIntProperty(System.getProperties(), MirandaProperties.PROPERTY_NUMBER_OF_LISTENERS);
        ScheduleMessage scheduleMessage = new ScheduleMessage(getNode().getQueue(), delayBetweenRetries, this);
        send(Miranda.timer.getQueue(), scheduleMessage);

        return new RetryingState(getNode());
    }

}
