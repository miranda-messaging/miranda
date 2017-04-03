package com.ltsllc.miranda;

import java.io.Serializable;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/2/2017.
 */
abstract public class Key implements Serializable {
    public byte[] encrypt (String clearText) throws GeneralSecurityException{
        return encrypt(clearText.getBytes());
    }

    abstract byte[] encrypt (byte[] clearText) throws GeneralSecurityException;
    abstract byte[] decrypt (byte[] cipherText) throws GeneralSecurityException;
}
