package com.ltsllc.miranda;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/2/2017.
 */
public class PublicKey extends Key {
    private java.security.PublicKey securityPublicKey;

    public java.security.PublicKey getSecurityPublicKey() {
        return securityPublicKey;
    }

    public PublicKey (java.security.PublicKey publicKey) {
        this.securityPublicKey = publicKey;
    }

    public byte[] encrypt(byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getSecurityPublicKey());
        return cipher.doFinal(plainText);
    }

    public byte[] decrypt (byte[] cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getSecurityPublicKey());
        return cipher.doFinal(cipherText);
    }
}
