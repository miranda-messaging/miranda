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

package com.ltsllc.clcl.test;

import com.ltsllc.clcl.*;
import com.ltsllc.common.test.TestCase;

import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.Date;

public class EncryptionTestCase extends TestCase {
    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private CertificateSigningRequest csr;
    private Certificate certificate;

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(KeyPair keyPair) {
        this.keyPair = keyPair;
    }

    public void creaateKeyPair () throws EncryptionException {
        this.keyPair = KeyPair.newKeys();
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(Certificate certificate) {
        this.certificate = certificate;
    }

    public CertificateSigningRequest getCsr() {
        return csr;
    }

    public void setCsr(CertificateSigningRequest csr) {
        this.csr = csr;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public static DistinguishedName createDn () {
        DistinguishedName dn = new DistinguishedName();

        dn.setCountryCode("US");
        dn.setState("Colorado");
        dn.setCity("Denver");
        dn.setCompany("Long Term Software LLC");
        dn.setDivision("Development");
        dn.setName("foo.com");

        return dn;
    }

    public KeyPair createKeyPair (int keySize) throws GeneralSecurityException {
        DistinguishedName dn = createDn();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(keySize);
        java.security.KeyPair jsKeyPair = keyPairGenerator.genKeyPair();

        PublicKey publicKey = new PublicKey(jsKeyPair.getPublic());
        publicKey.setDn(dn);
        PrivateKey privateKey = new PrivateKey(jsKeyPair.getPrivate());
        privateKey.setDn(dn);

        setPublicKey(publicKey);
        setPrivateKey(privateKey);

        return new KeyPair(publicKey, privateKey);
    }

    public Certificate createCertificate () throws GeneralSecurityException, EncryptionException {
        KeyPair keyPair = createKeyPair(2048);

        CertificateSigningRequest csr = keyPair.createCertificateSigningRequest();

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date aYearFromNow = calendar.getTime();

        Certificate certificate = keyPair.getPrivateKey().sign(csr, now, aYearFromNow);
        setCertificate(certificate);

        return certificate;
    }
}
