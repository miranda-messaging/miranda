/*
 * Copyright  2017 Long Term Software LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.ltsllc.clcl;

import com.ltsllc.common.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * A Java key store.
 * <p>
 * <p>
 * This class makes bridging the gap from clcl to java.security a little easier.
 * </p>
 * <p>
 * <h3>Attributes</h3>
 * <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>filename</td>
 * <td>String</td>
 * <td>The file name of the JKS file this instance represents.</td>
 * </tr>
 * <tr>
 * <td>certificateChains</td>
 * <td>Map<String, Certificate[]></td>
 * <td>A map from the alias for a key, to the certificate chain for that key.</td>
 * </tr>
 * <tr>
 * <td>privateKeys</td>
 * <td>Map<String, PrivateKey></td>
 * <td>A map from the alias for a key, to the key itself.</td>
 * </tr>
 * <tr>
 * <td>certificates</td>
 * <td>Map<String, Certificate></td>
 * <td>A map from an alias for a certificate, to the certificate itself.</td>
 * </tr>
 * <tr>
 * <td>passwordString</td>
 * <td>String</td>
 * <td>The password for the JKS file.</td>
 * </tr>
 * </table>
 */
public class JavaKeyStore {
    private String filename;
    private Map<String, Certificate[]> certificateChains;
    private Map<String, KeyPair> keys;
    private Map<String, Certificate> certificates;
    private String passwordString;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Create an empty instance
     */
    public JavaKeyStore() {
        this.certificateChains = new HashMap<String, Certificate[]>();
        this.certificates = new HashMap<String, Certificate>();
        this.keys = new HashMap<String, KeyPair>();
    }

    /**
     * Create an instance with no password.
     */
    public JavaKeyStore(String filename) throws EncryptionException {
        initialize(filename, null);
    }

    /**
     * Create an instance and initialize it from a JKS file.
     *
     * @param filename The JKS file to use.
     * @param password The password for the JKS file.  This is also used for the passwords for the keys.
     * @throws EncryptionException If there is a problem loading the JKS file.
     */
    public JavaKeyStore(String filename, String password) throws EncryptionException {
        initialize(filename, password);
    }

    /**
     * Initialize the instance from a JKS file.
     * <p>
     * <p>
     * This constructor creates an empty instance and then calls {@link #load()} on it.
     * This will set the filename and passwordString attributes for the instance.
     * </p>
     *
     * @param filename The JKS file to use.
     * @param password The password for the JKS file.  This is also used for the passwords for the keys.
     * @throws EncryptionException If there is a problem loading the JKS file.
     */
    public void initialize(String filename, String password) throws EncryptionException {
        this.filename = filename;
        this.certificates = new HashMap<String, Certificate>();
        this.certificateChains = new HashMap<String, Certificate[]>();
        this.keys = new HashMap<String, KeyPair>();
        this.passwordString = password;

        load();
    }

    public Map<String, KeyPair> getKeys() {
        return keys;
    }

    public void setKeys(Map<String, KeyPair> keys) {
        this.keys = keys;
    }

    public String getPasswordString() {
        return passwordString;
    }

    public void setPasswordString(String passwordString) {
        this.passwordString = passwordString;
    }

    public void setCertificates(Map<String, Certificate> certificates) {
        this.certificates = certificates;
    }

    public void setCertificateChains(Map<String, Certificate[]> certificateChains) {
        this.certificateChains = certificateChains;
    }

    public Map<String, Certificate[]> getCertificateChains() {
        return certificateChains;
    }

    public Map<String, Certificate> getCertificates() {
        return certificates;
    }

    /**
     * This method adds a key pair to the instance.
     * <p>
     * <p>
     * If the supplied chain is null, this method will create a new, self-
     * signed certificate from the key pair and add it.
     * </p>
     *
     * @param alias            The alias for the key.
     * @param keyPair          The key pair to add.
     * @param certificateChain The certificate chain for the key pair.
     */
    public void add(String alias, KeyPair keyPair, Certificate[] certificateChain) throws EncryptionException {
        getKeys().put(alias, keyPair);

        if (certificateChain == null) {
            CertificateSigningRequest csr = keyPair.createCertificateSigningRequest();

            Date now = new Date();

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.YEAR, 1);
            Date aYearFromNow = calendar.getTime();

            Certificate certificate = keyPair.getPrivateKey().sign(csr, now, aYearFromNow);
            certificateChain = new Certificate[]{certificate};
        }

        getCertificateChains().put(alias, certificateChain);
    }


    public void add (String alias, Certificate certificate) {
        getCertificates().put(alias, certificate);
    }

    public Certificate[] getCertificateChain (String alias) {
        return getCertificateChains().get(alias);
    }

    public void addKeysToKeystore (KeyStore keyStore, String alias, KeyPair keyPair, Certificate[] chain) throws EncryptionException {
        try {
            java.security.cert.Certificate[] jscChain = toJscChain(chain);
            keyStore.setKeyEntry(alias, keyPair.getPrivateKey().getSecurityPrivateKey(), getPasswordString().toCharArray(), jscChain);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to add a key to a keystore", e);
        }
    }

    public void addKeysToKeystore(KeyStore keyStore) throws EncryptionException {
        for (String alias : getKeys().keySet()) {
            KeyPair keyPair = getKeys().get(alias);

            if (keyPair == null)
                continue;

            Certificate[] chain = getCertificateChain(alias);

            //
            // there must be at least one certificate in the chain, because that is how a keystore stores a public key
            //
            if (chain == null || chain.length < 1) {
                chain = new Certificate[]{ keyPair.createCertificate() };
            }

            addKeysToKeystore(keyStore, alias, keyPair, chain);
        }
    }

    /**
     * This method will store the instance in a JKS file determined by the filename attribute.
     *
     * @throws EncryptionException If there is a problem storing the instance.
     */
    public void store() throws EncryptionException {
        FileOutputStream fileOutputStream = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            addKeysToKeystore(keyStore);
            addCertificatesToKeystore(keyStore, getCertificates());
            fileOutputStream = new FileOutputStream(filename);
            keyStore.store(fileOutputStream, getPasswordString().toCharArray());
        } catch (IOException | GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to write keystore", e);
        } finally {
            Utils.closeIgnoreExceptions(fileOutputStream);
        }
    }


    /**
     * This method adds the supplied certificates to the supplied keystore.  All the parameters are expected
     * to be non-null.
     * <p>
     * <p>
     * Note that the method stores the java.security.cert.X509Certificate in the keystore,
     * not the com.ltsllc.clcl.Certificate.
     * </p>
     *
     * @param keyStore     The keystore to store the certicates in.
     * @param certificates The certificates to store.
     * @throws KeyStoreException If there is a problem storing the certificates.
     */
    public static void addCertificatesToKeystore(KeyStore keyStore, Map<String, Certificate> certificates)
            throws KeyStoreException {
        for (String alias : certificates.keySet()) {
            Certificate certificate = certificates.get(alias);
            keyStore.setCertificateEntry(alias, certificate.getCertificate());
        }
    }

    /**
     * Convert an array of com.ltsllc.clcl certificates into java.security.cert
     * certificates.
     *
     * @param oldChain The com.ltsllc.clcl certificates to convert.
     * @return The java.security.cert certificates
     */
    public static java.security.cert.Certificate[] toJscChain(Certificate[] oldChain) {
        java.security.cert.Certificate[] newChain = new java.security.cert.Certificate[oldChain.length];

        for (int index = 0; index < oldChain.length; index++) {
            newChain[index] = oldChain[index].getCertificate();
        }

        return newChain;
    }

    public PublicKey getPublicKey(String alias) {
        KeyPair keyPair = getKeys().get(alias);

        if (keyPair == null)
            return null;

        return keyPair.getPublicKey();
    }

    /**
     * @throws EncryptionException
     */
    public void load() throws EncryptionException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new EncryptionException("The file, " + filename + ", does not exist");
        }

        FileInputStream fileInputStream = null;

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            fileInputStream = new FileInputStream(file);
            keyStore.load(fileInputStream, getPasswordString().toCharArray());
            extract(keyStore);
        } catch (GeneralSecurityException | IOException e) {
            throw new EncryptionException("Exception trying to load keystore, " + filename, e);
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }
    }

    public void extract(KeyStore keyStore) throws EncryptionException {
        extractKeys(keyStore);
        extractCertificates(keyStore);
    }

    public void extractKeys(KeyStore keyStore) throws EncryptionException {
        try {
            Enumeration<String> enumeration = keyStore.aliases();

            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();

                java.security.PrivateKey privateKey = (java.security.PrivateKey) keyStore.getKey(alias, getPasswordString().toCharArray());

                if (privateKey == null)
                    continue;

                extractKeys(keyStore, alias, privateKey);
            }
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Exception extracting keys", e);
        }
    }

    public void extractKeys (KeyStore keyStore, String alias, java.security.PrivateKey jsPrivateKey) throws EncryptionException {
        try {
            java.security.cert.Certificate[] certificates = keyStore.getCertificateChain(alias);
            Certificate[] chain = toClclChain(certificates);
            getCertificateChains().put(alias, chain);
            java.security.PublicKey jsPublicKey = certificates[0].getPublicKey();
            PublicKey publicKey = new PublicKey(jsPublicKey);
            PrivateKey privateKey = new PrivateKey(jsPrivateKey);
            KeyPair keyPair = new KeyPair(publicKey, privateKey);
            getKeys().put(alias, keyPair);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Exception extracting keys", e);
        }
    }

    public KeyPair getKeyPair (String alias) {
        return getKeys().get(alias);
    }

    public void extractCertificates(KeyStore keyStore) throws EncryptionException {
        try {
            Map<String, Certificate> map = new HashMap<String, Certificate>();

            Enumeration<String> enumeration = keyStore.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();

                java.security.cert.X509Certificate jsCertificate = (java.security.cert.X509Certificate) keyStore.getCertificate(alias);
                Certificate certificate = new Certificate(jsCertificate);

                map.put(alias, certificate);
            }

            this.certificates = map;
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to extract certificates", e);
        }
    }

    public void extractChains(KeyStore keyStore) throws EncryptionException {
        try {
            Map<String, Certificate[]> map = new HashMap<String, Certificate[]>();

            Enumeration<String> enumeration = keyStore.aliases();
            while (enumeration.hasMoreElements()) {
                String alias = enumeration.nextElement();

                java.security.cert.Certificate[] certificates = (java.security.cert.Certificate[]) keyStore.getCertificateChain(alias);

                if (certificates != null) {
                    Certificate[] chain = toClclChain(certificates);
                    map.put(alias, chain);
                }
            }

            this.certificateChains = map;
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to extract certificate chains", e);
        }
    }

    public Certificate getCertificate(String alias) {
        return getCertificates().get(alias);
    }

    public static Certificate[] toClclChain(java.security.cert.Certificate[] oldChain) {
        if (oldChain == null)
            return null;

        Certificate[] newChain = new Certificate[oldChain.length];

        for (int i = 0; i < oldChain.length; i++) {
            if (!(oldChain[i] instanceof X509Certificate))
                throw new IllegalArgumentException("Certificate is not X509");

            newChain[i] = new Certificate((X509Certificate) oldChain[i]);
        }

        return newChain;
    }
}
