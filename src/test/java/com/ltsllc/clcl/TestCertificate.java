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

import com.ltsllc.commons.util.HexConverter;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;

public class TestCertificate {
    public static final String TEST_CERTIFICATE_PEM =
            "-----BEGIN CERTIFICATE-----\n" +
            "MIICfTCCAeagAwIBAgIkYTc5ZWMyOWEtYmE4NS00NGEyLWFkODYtYzllNTJkNDEw\n" +
            "OTg1MA0GCSqGSIb3DQEBBQUAMHMxCzAJBgNVBAYTAlVTMREwDwYDVQQIDAhDb2xv\n" +
            "cmFkbzEPMA0GA1UEBwwGRGVudmVyMRswGQYDVQQKDBJMb25nIFRlcm0gU29mdHdh\n" +
            "cmUxETAPBgNVBAsMCFJlc2VhcmNoMRAwDgYDVQQDDAdmb28uY29tMB4XDTE3MDcy\n" +
            "NzIyMDE1OFoXDTE4MDcyNzIyMDE1OFowczELMAkGA1UEBhMCVVMxETAPBgNVBAgM\n" +
            "CENvbG9yYWRvMQ8wDQYDVQQHDAZEZW52ZXIxGzAZBgNVBAoMEkxvbmcgVGVybSBT\n" +
            "b2Z0d2FyZTERMA8GA1UECwwIUmVzZWFyY2gxEDAOBgNVBAMMB2Zvby5jb20wgZ8w\n" +
            "DQYJKoZIhvcNAQEBBQADgY0AMIGJAoGBALXJWQHjrCLoQZfnd5dQo9FeeEhWQIGz\n" +
            "FPNMDYmGR5NdANkcRxlP0l5R4t31Q3J5O9gFO+NA7tkZWm9DRaM1TZuHpru2d5r8\n" +
            "5ECXTnKiGy/Lwcqkv9r9C+S8HW9qtutK/YO3tbaNSxKepKrFwb9l9xvNgUE0Q5Eq\n" +
            "pdMOPBjJkaFvAgMBAAEwDQYJKoZIhvcNAQEFBQADgYEAKe+E8uCyaQJl93PM8SbC\n" +
            "PHK0PnSPzDL/Oh1I7k5BZDUrKfgSLsSyR8Wdi7f2+9XjVl0OOalrrTK8oSvncTIn\n" +
            "JMWD9x7JSxULZfMpN8ZLb5U73pfmKSWgUJp09ptJG1FJawziuZDBWlzoWWWWyPWu\n" +
            "HYwYAX2EzEovVp6jOkguJ00=\n" +
            "-----END CERTIFICATE-----\n";

    private Certificate certificate;

    public Certificate getCertificate() {
        return certificate;
    }

    @Before
    public void setup () throws Exception {
        this.certificate = Certificate.fromPEM(TEST_CERTIFICATE_PEM);
    }

    public static final String TEST_OTHER_CERTIFICATE_PEM =
        "-----BEGIN CERTIFICATE-----\n" +
        "MIICgTCCAeqgAwIBAgIkZDcyOWYyZGQtNDFhYS00MTEyLWFjZWUtOWJiZjE0MTA5\n" +
        "ZjMyMA0GCSqGSIb3DQEBBQUAMHUxCzAJBgNVBAYTAkFVMRswGQYDVQQIDBJOb3J0\n" +
        "aGVybiBUZXJyaXRvcnkxFjAUBgNVBAcMDUFsaWNlIFNwcmluZ3MxDDAKBgNVBAoM\n" +
        "A0lCTTERMA8GA1UECwwIUmVzZWFyY2gxEDAOBgNVBAMMB2Jhci5jb20wHhcNMTcw\n" +
        "NzI4MDEzNzE3WhcNMTgwNzI4MDEzNzE3WjB1MQswCQYDVQQGEwJBVTEbMBkGA1UE\n" +
        "CAwSTm9ydGhlcm4gVGVycml0b3J5MRYwFAYDVQQHDA1BbGljZSBTcHJpbmdzMQww\n" +
        "CgYDVQQKDANJQk0xETAPBgNVBAsMCFJlc2VhcmNoMRAwDgYDVQQDDAdiYXIuY29t\n" +
        "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCR8cfgrf4ht6cNPPF8hLWt1OX1\n" +
        "tOgiHKYGPyF4jnlNJfiKw0cHavuoHrqEJlgSr2ssJaQneZzdwD9KZCwkwaGxWLh4\n" +
        "XEsRJEz0gHYXSxnfV/7Wn9cXDWqrVsbBVDVMXClvYwzMD1ndTm8EId33Cs0Sil+V\n" +
        "7edu+3+tQT7PnBQ9gwIDAQABMA0GCSqGSIb3DQEBBQUAA4GBAHvUjL4cL9k5dN+5\n" +
        "SsaMIdIyZ4S/Yn/ihlWYw0JCvlXXZWcfEhgost2lSk/wV8hkkhxw+8T6aDy2DZme\n" +
        "UdimM4+KcD7UPo9pDI/JISzIF4RvLiYdVrfc9YRmxzBZwdee+zQd6n7KZcX7ZvKG\n" +
        "S3Ok/XIAwYLMJ4Rk3Ztt/BOUrs+i\n" +
        "-----END CERTIFICATE-----\n";


    public void testEquals () throws Exception {
        Certificate temp = Certificate.fromPEM(TEST_OTHER_CERTIFICATE_PEM);

        assert (getCertificate().equals(getCertificate()));
        assert (!getCertificate().equals(temp));
    }

    // this also test getIssuer and getSubject

    public void testFromPem () throws Exception {
        DistinguishedName dn = getCertificate().getSubject();

        assert (dn.getCountryCode().equals("US"));
        assert (dn.getState().equals("Colorado"));
        assert (dn.getCity().equals("Denver"));
        assert (dn.getCompany().equals("Long Term Software"));
        assert (dn.getDivision().equals("Research"));
        assert (dn.getName().equals("foo.com"));

        dn = getCertificate().getIssuer();

        assert (dn.getCountryCode().equals("US"));
        assert (dn.getState().equals("Colorado"));
        assert (dn.getCity().equals("Denver"));
        assert (dn.getCompany().equals("Long Term Software"));
        assert (dn.getDivision().equals("Research"));
        assert (dn.getName().equals("foo.com"));

        X509Certificate x509Certificate = getCertificate().getCertificate();

        Date now = new Date();

        assert (x509Certificate.getNotBefore().getTime() < now.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date expired = calendar.getTime();

        assert (x509Certificate.getNotAfter().getTime() < expired.getTime());
    }


    public void testGetCertificate () throws Exception {
        X509Certificate x509Certificate = getCertificate().getCertificate();

        DistinguishedName dn = new DistinguishedName(x509Certificate.getSubjectDN());

        assert (dn.getCountryCode().equals("US"));
        assert (dn.getState().equals("Colorado"));
        assert (dn.getCity().equals("Denver"));
        assert (dn.getCompany().equals("Long Term Software"));
        assert (dn.getDivision().equals("Research"));
        assert (dn.getName().equals("foo.com"));

        dn = new DistinguishedName(x509Certificate.getIssuerDN());

        assert (dn.getCountryCode().equals("US"));
        assert (dn.getState().equals("Colorado"));
        assert (dn.getCity().equals("Denver"));
        assert (dn.getCompany().equals("Long Term Software"));
        assert (dn.getDivision().equals("Research"));
        assert (dn.getName().equals("foo.com"));

        Date now = new Date();

        assert (x509Certificate.getNotBefore().getTime() < now.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date expired = calendar.getTime();

        assert (x509Certificate.getNotAfter().getTime() < expired.getTime());
    }

    public static String TEST_SERIAL_NUMBER_HEX = "61373965633239612D626138352D343461322D616438362D633965353264343130393835";


    public void testGetSerialNumber () throws Exception {
        BigInteger bigInteger = getCertificate().getSerialnumber();
        String hex = HexConverter.toHexString(bigInteger.toByteArray());

        assert (hex.equals(TEST_SERIAL_NUMBER_HEX));
    }


    public void testToPem () throws Exception {
        Certificate certificate = Certificate.fromPEM(TEST_CERTIFICATE_PEM);
        assert (certificate.equals(getCertificate()));
    }
}
