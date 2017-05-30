package com.ltsllc.miranda.mina;

import com.ltsllc.miranda.MirandaException;

/**
 * Created by clarkhobbie on 5/30/17.
 */
public class SslException extends MirandaException {
    public enum Reasons {
        NoSession,
        Exception,
        CertificateNotFound
    }
    private Reasons reason;

    public SslException(Reasons reason) {
        this.reason = reason;
    }

    public SslException(Reasons reason, Throwable throwable) {
        super(throwable);

        this.reason = reason;
    }
}
