package com.ltsllc.miranda.newMina;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public class NewConnectionHandler extends IoHandlerAdapter {
    private NewNetworkListener newNetworkListener;

    public NewNetworkListener getNewNetworkListener() {
        return newNetworkListener;
    }

    public NewConnectionHandler (NewNetworkListener newNetworkListener) {
        this.newNetworkListener = newNetworkListener;
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        getNewNetworkListener().newConnection(session);
    }
}
