package com.ltsllc.miranda.mina;

import com.google.gson.Gson;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.Network;
import com.ltsllc.miranda.node.networkMessages.WireMessage;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.ssl.SslFilter;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.nio.charset.Charset;
import java.security.cert.Certificate;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public class ConnectionHandler extends IoHandlerAdapter {
    private static Gson gson = new Gson();

    private Certificate certificate;
    private Network network;
    private IoSession session;
    private boolean verified;
    private Charset charset;
    private Handle handle;

    public ConnectionHandler(Network network, Certificate certificate) {
        this.network = network;
        this.certificate = certificate;
        this.verified = false;
        this.charset = Charset.defaultCharset();
    }

    public static Gson getGson() {
        return gson;
    }

    public Charset getCharset() {
        return charset;
    }

    public boolean getVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
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

    public void verifyConnection() throws SslException {
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
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        this.handle = getNetwork().createHandle(session);
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        setSession(session);
        verifyConnection();
        IoBuffer ioBuffer = (IoBuffer) message;
        String json = ioBuffer.getString(getCharset().newDecoder());
        WireMessage wireMessage = getGson().fromJson(json, WireMessage.class);
        Class clazz = Class.forName(wireMessage.getClassName());
        WireMessage pass2 = (WireMessage) getGson().fromJson(json, clazz);
        getHandle().deliver(wireMessage);
    }
}
