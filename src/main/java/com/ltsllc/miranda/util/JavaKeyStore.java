package com.ltsllc.miranda.util;

import sun.security.tools.keytool.CertAndKeyGen;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;



/**
 * Created by Clark on 5/8/2017.
 */
public class JavaKeyStore {
    private KeyStore keyStore;

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public PrivateKey getPrivateKey (String alias) throws GeneralSecurityException {
        return (PrivateKey) getKeyStore().getKey(alias, "whatever".toCharArray());
    }

    public PublicKey getPublicKey (String alias) throws GeneralSecurityException {
        Certificate certificate = getKeyStore().getCertificate(alias);
        return certificate.getPublicKey();
    }

    public Certificate getCertificate (String alias) throws GeneralSecurityException {
        return getKeyStore().getCertificate(alias);
    }
}
