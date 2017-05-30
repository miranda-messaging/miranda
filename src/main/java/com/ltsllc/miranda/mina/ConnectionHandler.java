package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.newMina.NewMinaHandle;
import com.ltsllc.miranda.newMina.NewNetwork;
import com.ltsllc.miranda.newMina.SslException;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.security.cert.Certificate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public class ConnectionHandler extends IoHandlerAdapter {
    private Certificate certificate;
    private Handle handle;
    private Network network;
    private IoSession session;
    private boolean verified;

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public IoSession getSession() {
        return session;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public Network getNetwork() {
        return network;
    }

    public Handle getHandle() {
        return handle;
    }

    public void setHandle(Handle handle) {
        this.handle = handle;
    }

    public ConnectionHandler(Network network) {
        this.network = network;
        this.verified = false;
    }

    public void verifyConnection () throws SslException {
        if (!getVerified()) {
            setVerified(false);

            SSLSession session = (SSLSession) getSession().getAttribute(SslFilter.SSL_SESSION);
            if (null == session) {
                throw new SslException(SslException.Reasons.NoSession);
            }

            try {
                Certificate[] certificates = session.getPeerCertificates();
                for (Certificate certificate : certificates) {
                    if (certificate.equals(getCertificate())) {
                        setVerified(true);
                    }
                }

                if (!getVerified()) {
                    throw new SslException(SslException.Reasons.CertificateNotFound);
                }

            } catch (SSLPeerUnverifiedException e) {
                throw new SslException(SslException.Reasons.Exception, e);
            }
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        verifyConnection();
        BlockingQueue<Message> queue = new LinkedBlockingQueue<Message>();
        com.ltsllc.miranda.newMina.NewMinaHandle handle = new NewMinaHandle(session, queue);
        getNetwork().newConnection(handle);
    }
}
