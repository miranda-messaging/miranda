package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.network.ConnectedMessage;
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
        logger.info("got connection to " + connectedMessage.getChannel().remoteAddress());

        getNode().setChannel(connectedMessage.getChannel());

        JoinWireMessage joinWireMessage = new JoinWireMessage(getNode());
        sendOnWire(joinWireMessage);

        return new JoiningState(getNode());
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
