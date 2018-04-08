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

import com.ltsllc.commons.util.Utils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/2/2017.
 */
public class PublicKey extends Key {
    public static final String ALGORITHM = "RSA";
    public static final String SESSION_ALGORITHM = "AES";

    private java.security.PublicKey securityPublicKey;


    public java.security.PublicKey getSecurityPublicKey() {
        return securityPublicKey;
    }

    public PublicKey(java.security.PublicKey publicKey) {
        this.securityPublicKey = publicKey;
    }


    @Override
    public String getSessionAlgorithm() {
        return SESSION_ALGORITHM;
    }

    @Override
    public byte[] encrypt(byte[] plainText) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecurityPublicKey());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);

            cipherOutputStream.write(plainText);
            cipherOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (GeneralSecurityException | IOException e) {
            throw new EncryptionException("Exception trying to decrypt", e);
        }
    }


    @Override
    public byte[] decrypt(byte[] cipherText) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecurityPublicKey());

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);

            cipherOutputStream.write(cipherText);
            cipherOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (GeneralSecurityException | IOException e) {
            throw new EncryptionException("Exception trying to decrypt", e);
        }
    }

    public EncryptedMessage toEncryptedMessage(byte[] plainText) throws EncryptionException {
        return encrypt(SESSION_ALGORITHM, plainText);
    }

    public CertificateSigningRequest createCertificateSigningRequest(PrivateKey privateKey) throws EncryptionException {
        return new CertificateSigningRequest(this, privateKey);
    }

    public String toPem() throws EncryptionException {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(getSecurityPublicKey());
            pemWriter.close();

            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to covert public key to PEM", e);
        }
    }


    public static PublicKey fromPEM(String pem) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser pemParser = new PEMParser(stringReader);
            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            jcaPEMKeyConverter.setProvider(new BouncyCastleProvider());
            java.security.PublicKey jsPublicKey = jcaPEMKeyConverter.getPublicKey(subjectPublicKeyInfo);
            return new PublicKey(jsPublicKey);
        } catch (IOException e) {
            throw new EncryptionException("Exception reading PEM", e);
        }
    }

    public static java.security.PublicKey readPublicKeyFromPEM(String pem) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser pemParser = new PEMParser(stringReader);
            SubjectPublicKeyInfo subjectPublicKeyInfo = (SubjectPublicKeyInfo) pemParser.readObject();
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            jcaPEMKeyConverter.setProvider(new BouncyCastleProvider());
            return jcaPEMKeyConverter.getPublicKey(subjectPublicKeyInfo);
        } catch (IOException e) {
            throw new EncryptionException("Exception reading PEM", e);
        }
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof PublicKey))
            return false;

        PublicKey other = (PublicKey) o;
        return getSecurityPublicKey().equals(other.getSecurityPublicKey());
    }

    public static void writePemFile(String filename, java.security.PublicKey publicKey) throws EncryptionException {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(publicKey);
            pemWriter.close();

        } catch (IOException e) {
            throw new EncryptionException("Exception trying to covert public key to PEM", e);
        }
    }
}
