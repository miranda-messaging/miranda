package com.ltsllc.miranda;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/3/2017.
 */
public class PrivateKey extends Key {
    private java.security.PrivateKey securityPrivateKey;

    public java.security.PrivateKey getSecurityPrivateKey() {
        return securityPrivateKey;
    }

    public PrivateKey (java.security.PrivateKey privateKey) {
        securityPrivateKey = privateKey;
    }

    public byte[] encrypt(byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, securityPrivateKey);
        return cipher.doFinal(plainText);
    }

    public byte[] decrypt (byte[] cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, securityPrivateKey);
        return cipher.doFinal(cipherText);
    }
}
