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

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class TestPrivateKey extends TestCase {
    private PrivateKey privateKey;
    private PublicKey publicKey;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Before
    public void setup () throws GeneralSecurityException, IOException {
        initalizeKeys();
    }

    public static final String TEST_ENCODED_PRIVATE_KEY_HEX_STRING = "30820277020100300D06092A864886F70D0101010500048202613082025D02010002818100B5C95901E3AC22E84197E7779750A3D15E7848564081B314F34C0D898647935D00D91C47194FD25E51E2DDF54372793BD8053BE340EED9195A6F4345A3354D9B87A6BBB6779AFCE440974E72A21B2FCBC1CAA4BFDAFD0BE4BC1D6F6AB6EB4AFD83B7B5B68D4B129EA4AAC5C1BF65F71BCD81413443912AA5D30E3C18C991A16F02030100010281801848DCA61EF718F6B40489FC74EF65E30B5EC0331D8CF6127F6A1288E3B25E225781A9806A063AD134D93CD38DFE15EA03B2B74C659942D15B3E6FDB36B1613B82776A4F450823354BF553A6A85E0491A482A3C25ABB8566E7EF3A5944F690E33335F15771D5096096F3C6180FF3E467CA6DDFF8D0731ED71D723BF4550AB369024100FB0C92E22F3789C71EDA752C77F7D07F8A7596251B1FA02C3D15C5B6999D2C5AAF11855812D3842665B1CC19AE1A0AF09558C9F3E7FCD586F6FAE9DD4AA3DB0D024100B95F19B120334121130A6F5D9D69D7F424425D51E5AD2B8E00016D294D0F41D1B3409EEA727FE248A2074D3AFFD76391190E57A6CDCB623E1BFC1F30F8289F6B024100BFCEE38A9CB8BA33C086F86F7959859B6C965A990F1822B0AA1B306C4B01A319C61884E0BDC18D7E28C4A74417991E32A268AC2406634E6147E27D3BEE0333210240086464CE5F0DD2FA0359AA970B645A51843EB8E8D74412BFB8025885D1264AAD8AB6F73AD7FA302D67A07AFF9BCB8D876921FC17E2233E0C5FC9F743894895870241008DE3CF0EE2BCA9F244E52E7073CA9FB20F15B2ECAFDF44F523F8EDB6A6E3D2F8E0DA0D45781D90627FCB890326C9E3F49F7E87C4AA78D794C8E7C8035449C573";
    public static final String TEST_ENCODED_PUBLIC_KEY_HEX_STRING = "30819F300D06092A864886F70D010101050003818D0030818902818100B5C95901E3AC22E84197E7779750A3D15E7848564081B314F34C0D898647935D00D91C47194FD25E51E2DDF54372793BD8053BE340EED9195A6F4345A3354D9B87A6BBB6779AFCE440974E72A21B2FCBC1CAA4BFDAFD0BE4BC1D6F6AB6EB4AFD83B7B5B68D4B129EA4AAC5C1BF65F71BCD81413443912AA5D30E3C18C991A16F0203010001";

    public void initalizeKeys () throws IOException, GeneralSecurityException {
        byte[] encoded = HexConverter.toByteArray(TEST_ENCODED_PUBLIC_KEY_HEX_STRING);
        EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(encoded);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        java.security.PublicKey jsPublicKey = keyFactory.generatePublic(encodedKeySpec);
        this.publicKey = new PublicKey(jsPublicKey);

        encoded = HexConverter.toByteArray(TEST_ENCODED_PRIVATE_KEY_HEX_STRING);
        encodedKeySpec = new PKCS8EncodedKeySpec(encoded);
        java.security.PrivateKey jsPrivateKey = keyFactory.generatePrivate(encodedKeySpec);
        this.privateKey = new PrivateKey(jsPrivateKey);
    }

    public void createKeyPair () throws GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        java.security.KeyPair keyPair = keyPairGenerator.generateKeyPair();
        java.security.PrivateKey jsPrivateKey = keyPair.getPrivate();
        this.privateKey = new PrivateKey(jsPrivateKey);
        this.publicKey = new PublicKey(keyPair.getPublic());
    }

    public static final String TEST_MESSAGE = "The Magic Words are Squeamish Ossifrage";
    public static final String TEST_ALGORITHM = "AES";


    public void testEncrypt () throws Exception {
        byte[] plainText = TEST_MESSAGE.getBytes();
        byte[] cipherText = getPrivateKey().encrypt(plainText);
        plainText = getPublicKey().decrypt(cipherText);
        String plainTextString = new String(plainText);
        assert (plainTextString.equals(TEST_MESSAGE));
    }


    public void testEncryptMessage () throws EncryptionException {
        EncryptedMessage encryptedMessage = getPrivateKey().encrypt(TEST_ALGORITHM, TEST_MESSAGE.getBytes());
        byte[] plainText = getPublicKey().decrypt(encryptedMessage);
        String string = new String(plainText);
        assert (string.equals(TEST_MESSAGE));
    }


    public void testDecrypt () throws Exception {
        byte[] cipherText = getPublicKey().encrypt(TEST_MESSAGE.getBytes());
        byte[] plainText = getPrivateKey().decrypt(cipherText);
        String message = new String(plainText);
        assert (message.equals(TEST_MESSAGE));
    }



    public void testDecryptMessage () throws Exception {
        EncryptedMessage encryptedMessage = getPublicKey().toEncryptedMessage(TEST_MESSAGE.getBytes());
        byte[] plainText = getPrivateKey().decrypt(encryptedMessage);
        String message = new String(plainText);
        assert (message.equals(TEST_MESSAGE));
    }

    public static final String TEST_PEM =
            "-----BEGIN RSA PRIVATE KEY-----\r\n" +
            "MIICXQIBAAKBgQC1yVkB46wi6EGX53eXUKPRXnhIVkCBsxTzTA2JhkeTXQDZHEcZ\r\n" +
            "T9JeUeLd9UNyeTvYBTvjQO7ZGVpvQ0WjNU2bh6a7tnea/ORAl05yohsvy8HKpL/a\r\n" +
            "/QvkvB1varbrSv2Dt7W2jUsSnqSqxcG/ZfcbzYFBNEORKqXTDjwYyZGhbwIDAQAB\r\n" +
            "AoGAGEjcph73GPa0BIn8dO9l4wtewDMdjPYSf2oSiOOyXiJXgamAagY60TTZPNON\r\n" +
            "/hXqA7K3TGWZQtFbPm/bNrFhO4J3ak9FCCM1S/VTpqheBJGkgqPCWruFZufvOllE\r\n" +
            "9pDjMzXxV3HVCWCW88YYD/PkZ8pt3/jQcx7XHXI79FUKs2kCQQD7DJLiLzeJxx7a\r\n" +
            "dSx399B/inWWJRsfoCw9FcW2mZ0sWq8RhVgS04QmZbHMGa4aCvCVWMnz5/zVhvb6\r\n" +
            "6d1Ko9sNAkEAuV8ZsSAzQSETCm9dnWnX9CRCXVHlrSuOAAFtKU0PQdGzQJ7qcn/i\r\n" +
            "SKIHTTr/12ORGQ5Xps3LYj4b/B8w+CifawJBAL/O44qcuLozwIb4b3lZhZtsllqZ\r\n" +
            "DxgisKobMGxLAaMZxhiE4L3BjX4oxKdEF5keMqJorCQGY05hR+J9O+4DMyECQAhk\r\n" +
            "ZM5fDdL6A1mqlwtkWlGEPrjo10QSv7gCWIXRJkqtirb3Otf6MC1noHr/m8uNh2kh\r\n" +
            "/BfiIz4MX8n3Q4lIlYcCQQCN488O4ryp8kTlLnBzyp+yDxWy7K/fRPUj+O22puPS\r\n" +
            "+ODaDUV4HZBif8uJAybJ4/SffofEqnjXlMjnyANUScVz\r\n" +
            "-----END RSA PRIVATE KEY-----\r\n";

    // also tests fromPem
    public void testToPem () throws EncryptionException {
        String pem = getPrivateKey().toPem();
        PrivateKey privateKey = PrivateKey.fromPEM(pem);
        assert (getPrivateKey().equals(privateKey));
    }

    public static final String TEST_PASSWORD = "whatever";

    public static final String TEST_ENCRYPTED_PEM =
            "-----BEGIN ENCRYPTED PRIVATE KEY-----\r\n" +
            "MIICrjAoBgoqhkiG9w0BDAEDMBoEFBXzB/XxiByPIw7AN7cZihfN8/IuAgIEAASC\r\n" +
            "AoDizP+AAJhXQJAq3xNHMkHXWhABkxls8GDbbUb2gfS3rMpgZqo6KU1ZqvtOTfgr\r\n" +
            "sD9nxfqQ211VjZh0P9Zg7duxY4fuxuLZhG6Yv8TJswUrRhdtm1PuZLRR7ZJuOmHb\r\n" +
            "ZlqqmUgcHPH4SawKwVys/7a5XHsKTUzfJgzeYZzHOTiovwZAWb/Of8CWgH48gLU+\r\n" +
            "GByKjKG5xd67mZhx5HEN6TffT0yPzIlnsHksXFJ80j/dr4fxgmXtOb2ykvAQrK3c\r\n" +
            "xaGk0y+e+RUoX0w2YSXAvQGeTiRvQXqN/b4R2QrmhznJdq6pUfiBBL3TU2/WqTw1\r\n" +
            "SPxMgAq89ZXVR/QmI6UV13PLq3x8SgSMyrYLR0+OfPtzM9fZwSwx42zZkuxjaEVW\r\n" +
            "cPj8UaH1m9v8I/2KtPlcaSIFhi5ioBmWostVeqT51SweKHggh9BSEhajrXdXDw4J\r\n" +
            "WSo1CUwZsehLhA7fgo5eRPyoyK4mDfrgKrloPM0wvIx54A9j/Z2pljjm1KCa/8/E\r\n" +
            "G7rGzME67M3iG6dDSQE6YqNXd7miYwFmZwYje49cX5zcMkkOpuCeEDl+vq3IjwuH\r\n" +
            "9ECJWYefA8UOqh4jQbtAipHvfpSVcOaOdzG5zV5F2uIDKETQEQoVRnnnr0jUTv/7\r\n" +
            "n0fzRvN0xsILISB3iMeIHBVRVNf/7CKMmZd/OcdaGi3DOUUN6p+sPu84dzx8zC1H\r\n" +
            "H30m5/8p/DP1rJEp/08lfnjV2lr686204QFhXCkiRopT5qWZNyqK2hXtCAtzzIaH\r\n" +
            "rW8ZMRNy3t3wn0dSU+gK3dHhefeR77DBv6HT+Ddh1Jpzcp2PuAMjKvuU2XPPzbai\r\n" +
            "9IXB4izIRiG59mVRFk6d0fqI\r\n" +
            "-----END ENCRYPTED PRIVATE KEY-----\r\n";


    public void testToPasswordProtectedPem () throws Exception {
        String pem = getPrivateKey().toPem(TEST_PASSWORD);
        PrivateKey temp = PrivateKey.fromPEM(pem, TEST_PASSWORD);
        assert(getPrivateKey().equals(temp));
    }


    public void testFromPemWithPassword () throws Exception {
        PrivateKey temp = PrivateKey.fromPEM(TEST_ENCRYPTED_PEM, TEST_PASSWORD);
        assert (getPrivateKey().equals(temp));
    }


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

    /*
    @Test
    public void testSign () throws Exception {
        DistinguishedName dn = new DistinguishedName();
        dn.setCountryCode("US");
        dn.setState("Colorado");
        dn.setCity("Denver");
        dn.setCompany("Long Term Software");
        dn.setDivision("Research");
        dn.setName("foo.com");

        getPrivateKey().setDn(dn);
        getPublicKey().setDn(dn);

        CertificateSigningRequest csr = getPublicKey().createCertificateSigningRequest(getPrivateKey());
        Calendar calendar = Calendar.getInstance();

        Date now = new Date();
        calendar.setTime(now);
        calendar.add(Calendar.YEAR, 1);
        Date until = calendar.getTime();

        Certificate certificate = getPrivateKey().sign(csr, now, until);
        String pem = certificate.toPem();
        Certificate temp = Certificate.fromPEM (TEST_CERTIFICATE_PEM);
        assert (temp.equals(certificate));
    }
    */

    public static final String TEST_PASSWORD_PROTECTED_PEM =
            "-----BEGIN ENCRYPTED PRIVATE KEY-----\n" +
            "MIICrjAoBgoqhkiG9w0BDAEDMBoEFGpm+gvLXIOti7OJ3I5Dx53KjUBqAgIEAASC\n" +
            "AoAwgj/u7sA5xzaG2mMD7XoNt+MgkBuDAm4dtdISLgKeVqaplwRpdfp6rKOKiBGh\n" +
            "YaFsogmzpHUqITnx/9IFjMtqSs7mdwp11nrHFyGn4bj2eZgwM05ZjFPZieOmUWLz\n" +
            "96sS1LMQ/xhGvHAP7a9c60q0oqUmOgwfsOTr/vzjCt6Edb1IbemFKi5JjPQIN1N3\n" +
            "C0D8Fwwb9gll87bbkI0ohMXnfPHsHAi2CP9+fTXtxsI+9T08Cvbk2G97XuRVlaD9\n" +
            "0EINKws7jQP3aQ5xJWv9mqkjlJ2h/f5PzMsWDyPFkWdnN7iUky0HRS9f4el0F7Mn\n" +
            "IhelPPaBy//U7Cln13opmSLUqhot6cPUB7edABKPIn0dsPjIcS9wF3bbxCq7ay9t\n" +
            "9jYx3RoXdrx01i8CKEjEPIduQ5cBHNNWYqoxxAD5KsowSL+Cxu3nXdDEmUtpXZBn\n" +
            "YjCeGzCHl46iPO/Ny2bA3mzVu6YUFIldzCePNijDotS6FHzywcKtzyARt9eP1aY8\n" +
            "2WxthmD8+Zb9jwsffUm/HbSz1GbgGo1Lenh9iTYbmmCXZC4XDIgex2+N1UYOg6PN\n" +
            "u81EABeFqov5U3AkC2JNqmvp029h32dBQWbv2lxKZAUMnJxi0ttHuhLwXoQHei0U\n" +
            "Soo/5QtK13BYbenu2RcNck7OoWX1YfG/xG1E2wHuZeIGFalhlfm9BfXbMd0iwTS3\n" +
            "TWsvyRJbEDF1U75/bA1eneD6XopXVNFch5z32T0tYKjpxfV2oYK2P1Xpqf8+UPX4\n" +
            "VBySukLpQ6h1jJpSPXppGv8RbrCUo5c6V4DqtOaBi/zcOU0hiH132SFFkoQYBgId\n" +
            "WuhYe/VaDam4cI+qqw8/UrAO\n" +
            "-----END ENCRYPTED PRIVATE KEY-----\n";

    public void testToPemWithPassword () throws EncryptionException {
        String pem = getPrivateKey().toPem(TEST_PASSWORD);
        PrivateKey temp = PrivateKey.fromPEM(pem, TEST_PASSWORD);
        assert (temp.equals(getPrivateKey()));
    }


    public void testCreateSerialNumber () throws EncryptionException {
        BigInteger bigInteger = getPrivateKey().createSerialNumber();
        BigInteger different = getPrivateKey().createSerialNumber();

        assert (bigInteger.equals(bigInteger));
        assert (!(bigInteger.equals(different)));
    }
}
