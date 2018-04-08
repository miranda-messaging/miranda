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
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.PKCS8Generator;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.*;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import java.io.*;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Clark on 4/3/2017.
 */

/**
 * A class to simplify working with private keys.
 *
 * <p>
 * This class was created because of the state of interoperability between openSSL
 * and Java public key.
 * </p>
 *
 * <p>
 * The class provides a number of "convenience method" for doing common tasks, specifically:
 * </p>
 *
 * <ul>
 * <li>{@link #encrypt(byte[])} for easy encryption</li>
 * <li>{@link #decrypt(byte[])} for easy decryption</li>
 * <li>{@link #encrypt(String, byte[])} for "fast" encryption of large amounts of data</li>
 * <li>{@link #decrypt(EncryptedMessage)} for "fast" decryption of large amounts of data</li>
 * </ul>
 *
 * <h3>Properties</h3>
 * <table border="1">
 * <tr>
 * <th>Name</th>
 * <th>Type</th>
 * <th>Description</th>
 * </tr>
 *
 * <tr>
 * <td>securityPrivateKey</td>
 * <td>java.security.PrivateKey</td>
 * <td>
 * The underlying private key that this instance "wraps."
 * An instance should always have one of these.
 * </td>
 * </tr>
 * </table>
 */
public class PrivateKey extends Key {
    public static final String ALGORITHM = "RSA";
    public static final String SESSION_ALGORITHM = "AES";

    private java.security.PrivateKey securityPrivateKey;

    public static void writePemFile(String filename, java.security.PrivateKey privateKey) throws IOException {
        StringWriter stringWriter = new StringWriter();
        PEMWriter pemWriter = new PEMWriter(stringWriter);
        pemWriter.writeObject(privateKey);
        pemWriter.close();

        Utils.writeTextFile(filename, stringWriter.toString());
    }

    public java.security.PrivateKey getSecurityPrivateKey() {
        return securityPrivateKey;
    }

    public PrivateKey(java.security.PrivateKey privateKey) {
        securityPrivateKey = privateKey;
    }

    @Override
    public String getSessionAlgorithm() {
        return SESSION_ALGORITHM;
    }

    /**
     * Convenience method to encrypt some data.
     *
     * <p>
     * This method hides all the dirty work associated with encrypting some data with public key.
     * </p>
     *
     * @param plainText
     * @return
     * @throws EncryptionException
     */
    @Override
    public byte[] encrypt(byte[] plainText) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecurityPrivateKey());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
            cipherOutputStream.write(plainText);
            cipherOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (GeneralSecurityException | IOException e) {
            throw new EncryptionException("Exception trying to encrypt", e);
        }
    }

    @Override
    public byte[] decrypt(byte[] cipherText) throws EncryptionException {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecurityPrivateKey());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
            cipherOutputStream.write(cipherText);
            cipherOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (GeneralSecurityException | IOException e) {
            throw new EncryptionException("Exception trying to decrypt", e);
        }
    }

    public byte[] decrypt(EncryptedMessage encryptedMessage) throws EncryptionException {
        return super.decrypt(encryptedMessage);
    }

    @Override
    public String toPem() throws EncryptionException {
        try {
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(getSecurityPrivateKey());
            pemWriter.close();

            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to convert private key to PEM", e);
        }
    }


    public String toPem(String password) throws EncryptionException {
        try {
            DESedeEngine desEdeEngine = new DESedeEngine();
            CBCBlockCipher cbcBlockCipher = new CBCBlockCipher(desEdeEngine);
            BcPKCS12PBEOutputEncryptorBuilder bcPKCS12PBEOutputEncryptorBuilder = new BcPKCS12PBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd3_KeyTripleDES_CBC, cbcBlockCipher);
            OutputEncryptor outputEncryptor = bcPKCS12PBEOutputEncryptorBuilder.build(password.toCharArray());
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(getSecurityPrivateKey().getEncoded());
            PKCS8Generator pkcs8Generator = new PKCS8Generator(privateKeyInfo, outputEncryptor);
            StringWriter stringWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(stringWriter);
            pemWriter.writeObject(pkcs8Generator);
            pemWriter.close();
            return stringWriter.toString();
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to convert private key to PEM", e);
        }
    }

    public static PrivateKey fromPEM(String pem, String passwordString) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser pemParser = new PEMParser(stringReader);
            Object o = pemParser.readObject();

            PKCS8EncryptedPrivateKeyInfo pkcs8EncryptedPrivateKeyInfo =
                    (PKCS8EncryptedPrivateKeyInfo) o;

            BcPKCS12PBEInputDecryptorProviderBuilder bcPKCS12PBEInputDecryptorProviderBuilder =
                    new BcPKCS12PBEInputDecryptorProviderBuilder();

            InputDecryptorProvider inputDecryptorProvider =
                    bcPKCS12PBEInputDecryptorProviderBuilder.build(passwordString.toCharArray());

            PrivateKeyInfo privateKeyInfo = pkcs8EncryptedPrivateKeyInfo.decryptPrivateKeyInfo(inputDecryptorProvider);

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider(new BouncyCastleProvider());
            java.security.PrivateKey jsPrivateKey = (RSAPrivateCrtKey) converter.getPrivateKey(privateKeyInfo);
            return new PrivateKey(jsPrivateKey);
        } catch (Exception e) {
            throw new EncryptionException("Exception trying to convert private key to PEM", e);
        }
    }

    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    public BigInteger createSerialNumber() throws EncryptionException {
        try {
            byte[] bytes = UUID.randomUUID().toString().getBytes();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(bytes);
            byteArrayOutputStream.close();

            bytes = byteArrayOutputStream.toByteArray();

            return new BigInteger(bytes);
        } catch (IOException e) {
            throw new EncryptionException("Exception creating serial number", e);
        }
    }

    public Certificate sign(CertificateSigningRequest certificateSigningRequest, Date notValidBefore,
                            Date notValidAfter) throws EncryptionException {
        try {
            AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find(SIGNATURE_ALGORITHM);
            AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);

            AsymmetricKeyParameter asymmetricKeyParameter = PrivateKeyFactory.
                    createKey(getSecurityPrivateKey().getEncoded());

            SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(certificateSigningRequest.getPkcs10().
                    getSubjectPublicKeyInfo().getEncoded());

            X500Name issuer = new X500Name(getDn().toString());
            X500Name subject = new X500Name(certificateSigningRequest.getSubjectDn().toString());
            BigInteger serialNumber = createSerialNumber();
            X509v3CertificateBuilder myCertificateGenerator = new X509v3CertificateBuilder(issuer, serialNumber,
                    notValidBefore, notValidAfter, subject, keyInfo);

            ContentSigner sigGen = new BcRSAContentSignerBuilder(sigAlgId, digAlgId).build(asymmetricKeyParameter);

            X509CertificateHolder holder = myCertificateGenerator.build(sigGen);
            org.bouncycastle.asn1.x509.Certificate certificate = holder.toASN1Structure();

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());

            InputStream inputStream = new ByteArrayInputStream(certificate.getEncoded());
            X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
            return new Certificate(x509Certificate);
        } catch (IOException | OperatorException | CertificateException e) {
            throw new EncryptionException("Exception trying to sign CSR", e);
        }
    }


    public static PrivateKey fromPEM(String pem) throws EncryptionException {
        try {
            StringReader stringReader = new StringReader(pem);
            PEMParser parser = new PEMParser(stringReader);
            PEMKeyPair pemKeyPair = (PEMKeyPair) parser.readObject();
            JcaPEMKeyConverter jcaPEMKeyConverter = new JcaPEMKeyConverter();
            jcaPEMKeyConverter.setProvider(new BouncyCastleProvider());
            java.security.PrivateKey privateKey = jcaPEMKeyConverter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
            return new PrivateKey(privateKey);
        } catch (IOException e) {
            throw new EncryptionException("Exception trying to convert PEM to private key", e);
        }
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof PrivateKey))
            return false;

        PrivateKey other = (PrivateKey) o;
        return getSecurityPrivateKey().equals(other.getSecurityPrivateKey());
    }
}
