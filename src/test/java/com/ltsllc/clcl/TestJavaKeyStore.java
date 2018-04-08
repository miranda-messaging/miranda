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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.security.KeyPairGenerator;
import java.util.Calendar;
import java.util.Date;

public class TestJavaKeyStore extends TestCase {
    public static final String TEST_ALIAS = "private";
    public static final String TEST_PASSWORD = "whatever";
    public static final String TEST_FILENAME = "test";

    private JavaKeyStore javaKeyStore;

    public JavaKeyStore getJavaKeyStore() {
        return javaKeyStore;
    }

    @Before
    public void setup () {
        this.javaKeyStore = new JavaKeyStore();
        this.javaKeyStore.setFilename(TEST_FILENAME);
    }

    @After
    public void cleanup () {
        delete(TEST_FILENAME);
    }

    // also tests load, store, initialize and extract

    public void testAddKeyPair () throws Exception {
        DistinguishedName dn = createDn();

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = new PublicKey(keyPair.getPublic());
        publicKey.setDn(dn);
        PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
        privateKey.setDn(dn);
        KeyPair keyPair2 = new KeyPair(publicKey, privateKey);

        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date aYearFromNow = calendar.getTime();

        CertificateSigningRequest csr = publicKey.createCertificateSigningRequest(privateKey);
        Certificate certificate = privateKey.sign(csr, now, aYearFromNow);

        Certificate[] chain = { certificate };

        getJavaKeyStore().add(TEST_ALIAS, keyPair2, chain);

        getJavaKeyStore().setFilename(TEST_FILENAME);
        getJavaKeyStore().setPasswordString(TEST_PASSWORD);
        getJavaKeyStore().store();

        getJavaKeyStore().load();

        KeyPair returnedValue = getJavaKeyStore().getKeyPair(TEST_ALIAS);
        assert (keyPair2.equals(returnedValue));
    }


    public void testAddCertificate () throws Exception {
        Certificate certificate = createCertificate();
        getJavaKeyStore().add(TEST_ALIAS, certificate);

        Certificate anotherCert = getJavaKeyStore().getCertificate(TEST_ALIAS);
        assert (certificate.equals(anotherCert));
    }


    // This also tests load, store, initialize, extract, extractCertificates and getCertificate
    public void testAddCertificates () throws Exception{
        Certificate certificate = createCertificate();
        getJavaKeyStore().setPasswordString(TEST_PASSWORD);
        getJavaKeyStore().add(TEST_ALIAS, certificate);
        getJavaKeyStore().store();

        getJavaKeyStore().load();

        Certificate otherCertificate = getJavaKeyStore().getCertificate(TEST_ALIAS);
        assert (otherCertificate.equals(certificate));
    }
}
