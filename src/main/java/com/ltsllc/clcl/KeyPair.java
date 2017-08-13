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

import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.*;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;
import org.bouncycastle.util.io.pem.PemObject;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.Date;

public class KeyPair {
    public static final String ALGORITHM = "RSA";
    public static final String SESSION_ALGORITHM = "AES";
    public static final String OPEN_SSL_ALGORITHM = "DESede/CBC/PKCS5Padding";


    private PublicKey publicKey;
    private PrivateKey privateKey;

    public KeyPair(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public static KeyPair newKeys () throws EncryptionException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PublicKey publicKey = new PublicKey(keyPair.getPublic());
            PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
            return new KeyPair(publicKey, privateKey);
        } catch (GeneralSecurityException e) {
            throw new EncryptionException("Exception trying to generate new keys", e);
        }
    }

    public String toPem () throws EncryptionException {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            java.security.KeyPair jsKeyPair = new java.security.KeyPair(getPublicKey().getSecurityPublicKey(),
                    getPrivateKey().getSecurityPrivateKey());

            pemWriter.writeObject(jsKeyPair);
            pemWriter.close();

            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to convert key pair to PEM", e);
        }
    }

    public static KeyPair fromPem (String pem) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser pemParser = new PEMParser(stringReader);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            jcaPEMKeyConverter.setProvider(new BouncyCastleProvider());
            java.security.KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);
            PublicKey publicKey = new PublicKey(keyPair.getPublic());
            PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
            return new KeyPair(publicKey, privateKey);
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to create PEM", e);
        }
    }
/*
    public String toPem (String password) throws EncryptionException {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);

            BcPKCS12PBEOutputEncryptorBuilder bcPKCS12PBEOutputEncryptorBuilder = new BcPKCS12PBEOutputEncryptorBuilder()
            java.security.KeyPair keyPair = new java.security.KeyPair(getPublicKey().getSecurityPublicKey(), getPrivateKey().getSecurityPrivateKey());
            PKCS8Generator pkcs8Generator = new PKCS8Generator(getPrivateKey().getSecurityPrivateKey())
            pemWriter.writeObject(keyPair, pemEncryptor);
            pemWriter.close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to create PEM", e);
        }
    }
*/

    public String toPem (String password) throws EncryptionException {
        try {
            java.security.KeyPair keyPair = new java.security.KeyPair(getPublicKey().getSecurityPublicKey(), getPrivateKey().getSecurityPrivateKey());
            DESedeEngine desEdeEngine = new DESedeEngine();
            CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(desEdeEngine);
            BcPKCS12PBEOutputEncryptorBuilder bcPKCS12PBEOutputEncryptorBuilder = new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, cbcBlockCipher);
            OutputEncryptor outputEncryptor = bcPKCS12PBEOutputEncryptorBuilder.build(password.toCharArray());
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(keyPair);
            pemWriter.close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to convert private key to PEM", e);
        }
    }

    public static KeyPair fromPem (String pem, String password) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser pemParser = new PEMParser(stringReader);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            jcaPEMKeyConverter.setProvider(new BouncyCastleProvider());
            java.security.KeyPair keyPair = jcaPEMKeyConverter.getKeyPair(pemKeyPair);
            PublicKey publicKey = new PublicKey(keyPair.getPublic());
            PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
            return new KeyPair(publicKey, privateKey);
        } catch (IOException e) {
            throw new EncryptionException("Exception reading PEM", e);
        }
    }

    public CertificateSigningRequest createCertificateSigningRequest () throws EncryptionException {
        return getPublicKey().createCertificateSigningRequest(getPrivateKey());
    }

    public boolean equals (Object o) {
        if (o == null || !(o instanceof KeyPair))
            return false;

        KeyPair other = (KeyPair) o;
        return getPublicKey().equals(other.getPublicKey()) && getPrivateKey().equals(other.getPrivateKey());
    }

    public Certificate createCertificate() throws EncryptionException {
        CertificateSigningRequest csr = getPublicKey().createCertificateSigningRequest(getPrivateKey());

        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 10);
        Date tenYearsFromNow = calendar.getTime();

        return getPrivateKey().sign(csr, now, tenYearsFromNow);
    }
}
