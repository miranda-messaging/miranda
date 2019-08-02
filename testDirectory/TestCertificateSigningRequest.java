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

import org.junit.Before;
import org.junit.Test;
import sun.security.pkcs10.PKCS10;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class TestCertificateSigningRequest {
    public static String TEST_CSR_PEM
            = "-----BEGIN CERTIFICATE REQUEST-----\n" +
            "MIIBtjCCAR8CAQAwdjEQMA4GA1UEAxMHZm9vLmNvbTEUMBIGA1UECxMLRGV2ZWxv\n" +
            "cG1lbnQxGzAZBgNVBAoTEkxvbmcgVGVybSBTb2Z0d2FyZTEPMA0GA1UEBxMGRGVu\n" +
            "dmVyMREwDwYDVQQIEwhDb2xvcmFkbzELMAkGA1UEBhMCVVMwgZ8wDQYJKoZIhvcN\n" +
            "AQEBBQADgY0AMIGJAoGBANEuXgrptxfLRocsPgscw5v3arlbCxHRZ885txGN+rzU\n" +
            "DaOYdFez87hn46PSQPY8MHTT6a0QcfETqD3l5ZpxcGxxd+OEUKkAC36uy1jYrurO\n" +
            "bv/c3CTl7N6dYZ/XHYIUyMAbgRk7HqKSW0Ntfo2wOBuB+SpKqj5+V+8jlD6iv3Nh\n" +
            "AgMBAAGgADANBgkqhkiG9w0BAQUFAAOBgQBEpL6V5+BdTsBydqcMOQADpzPmsdNV\n" +
            "NqbDiE7NqTIW7A4tZFrefihoDmKSd8j/G9+VwKQHf5syImXj8ReihPSWQTqb/tx7\n" +
            "cbWe67eTQeOQ0mEY/tG7j1rtFP7YMwN7oZ5lQyVjNFLMSkqlPCxXodJamQK+TzO8\n" +
            "KuUEAKjZCmVKEA==\n" +
            "-----END CERTIFICATE REQUEST-----\n";

    private CertificateSigningRequest certificateSigningRequest;

    public CertificateSigningRequest getCertificateSigningRequest() {
        return certificateSigningRequest;
    }

    @Before
    public void setup () throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
        PublicKey publicKey = new PublicKey(keyPair.getPublic());

        DistinguishedName dn = new DistinguishedName();

        dn.setCountryCode("US");
        dn.setState("Colorado");
        dn.setCity("Denver");
        dn.setCompany("Long Term Software");
        dn.setDivision("Development");
        dn.setName("foo.com");

        publicKey.setDn(dn);

        this.certificateSigningRequest = new CertificateSigningRequest(publicKey, privateKey);
    }



    public void testConstructor () throws Exception {
        DistinguishedName dn = new DistinguishedName();

        dn.setCountryCode("US");
        dn.setState("Colorado");
        dn.setCity("Denver");
        dn.setCompany("Long Term Software");
        dn.setDivision("Development");
        dn.setName("foo.com");

        assert (getCertificateSigningRequest().getSubjectDn().equals(dn));

        PKCS10 pkcs10 = getCertificateSigningRequest().getPkcs10();
        CertificateSigningRequest csr = new CertificateSigningRequest(pkcs10);
        assert (getCertificateSigningRequest().equals(csr));
    }



    public void testEquals () throws Exception {
        assert(getCertificateSigningRequest().equals(getCertificateSigningRequest()));

        DistinguishedName dn = new DistinguishedName();

        dn.setCountryCode("US");
        dn.setState("Colorado");
        dn.setCity("Denver");
        dn.setCompany("Long Term Software");
        dn.setDivision("Development");
        dn.setName("foo.com");

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PublicKey publicKey = new PublicKey(keyPair.getPublic());
        publicKey.setDn(dn);

        PrivateKey privateKey = new PrivateKey(keyPair.getPrivate());
        CertificateSigningRequest otherCsr = new CertificateSigningRequest(publicKey, privateKey);

        assert (!getCertificateSigningRequest().equals(otherCsr));
    }


    public static final String TEST_DN_STRING = "c=US,st=Colorado,l=Denver,o=Long Term Software,ou=Development,cn=foo.com";


    public void testFromPem () throws Exception {
        CertificateSigningRequest csr = CertificateSigningRequest.fromPem(TEST_CSR_PEM);

        assert (csr.equals(csr));
    }


    public void testGetSubjectDn () {
        DistinguishedName dn = new DistinguishedName();

        dn.setCountryCode("US");
        dn.setState("Colorado");
        dn.setCity("Denver");
        dn.setCompany("Long Term Software");
        dn.setDivision("Development");
        dn.setName("foo.com");

        assert (getCertificateSigningRequest().getSubjectDn().equals(dn));
    }

}
