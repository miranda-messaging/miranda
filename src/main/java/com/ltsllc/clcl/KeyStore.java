package com.ltsllc.clcl;

import com.ltsllc.commons.util.Utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that simplifies the use of a KeyStore
 */
public class KeyStore {
    private java.security.KeyStore jsKeyStore;
    private Map<String, Key> keyMap = new HashMap<>();
    private Map<String, Certificate> certificateMap = new HashMap<>();
    private String filename;
    private String password;
    private Map<String, Certificate[]> certificateChains = new HashMap<>();

    public String getFilename() {
        return filename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public KeyStore(String filename, String password) throws EncryptionException {
        try {
            jsKeyStore = java.security.KeyStore.getInstance("JKS");

            jsKeyStore.load(null, password.toCharArray());
            setFilename(filename);
            setPassword(password);
        } catch (GeneralSecurityException|IOException e) {
            throw new EncryptionException("error trying to create keystore", e);
        }
    }

    public void addKey(String alias, Key key) {
        keyMap.put(alias, key);
    }

    public void addPrivateKey(String alias, PrivateKey privateKey, Certificate[] certificateChain) {
        keyMap.put(alias, privateKey);
        certificateChains.put(alias, certificateChain);
    }

    public void addCertificate(String alias, Certificate certificate) {
        certificateMap.put(alias, certificate);
    }

    public void write() throws IOException, EncryptionException {
        try {
            for (String alias : keyMap.keySet()) {
                Key entry = keyMap.get(alias);

                Certificate[] certificates = null;
                if (entry instanceof PrivateKey) {
                    certificates = certificateChains.get(alias);
                }

                java.security.cert.Certificate[] jsChain = null;
                if (null != certificates) {
                    jsChain = certificateChainToJsCetificateChain(certificates);
                }

                java.security.Key key = entry.getJsEntry();

                jsKeyStore.setKeyEntry(alias, key, password.toCharArray(), jsChain);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(getFilename());
            jsKeyStore.store(fileOutputStream, getPassword().toCharArray());
        } catch (GeneralSecurityException keyStoreException) {
            throw new EncryptionException("Exception trying to store keystore", keyStoreException);
        }
    }

    public void read() throws EncryptionException {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(getFilename());
            jsKeyStore.load(fileInputStream, getPassword().toCharArray());

        } catch (IOException | GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to load a keystore", e);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

    }

    public java.security.cert.Certificate[] certificateChainToJsCetificateChain(Certificate[] certificates) {
        java.security.cert.Certificate[] jsChain = new java.security.cert.Certificate[certificates.length];
        int index = 0;

        for (Certificate certificate : certificates) {
            jsChain[index] = certificate.getJsCertificate();
            index++;
        }

        return jsChain;
    }

    public Key getKey(String alias) throws EncryptionException {
        if (null == keyMap.get(alias)) {
            read();
        }

        return keyMap.get(alias);
    }
}
