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

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Before;
import org.junit.Test;

import java.security.Provider;
import java.security.Security;

public class TestKey {
    public static final String TEST_ALGORITHM = "AES";
    public static final String TEST_MESSAGE = "TEST";
    public static final String TEST_PASSWORD = "whatever";

    private void setPublicKey (PublicKey publicKey) {
    }

    private PublicKey getPublicKey () {
        return null;
    }

    private void setPrivateKey (PrivateKey privateKey) {
    }

    private PrivateKey getPrivateKey () {
        return null;
    }

    @Before
    public void setup () throws Exception {
        KeyPair keyPair = KeyPair.newKeys();
        setPublicKey(keyPair.getPublicKey());
        setPrivateKey(keyPair.getPrivateKey());
    }

    public static boolean containsBouncyCastleProvider (Provider[] providers) {
        for (Provider provider : providers) {
            if (provider instanceof BouncyCastleProvider)
                return true;
        }

        return false;
    }


    public void testCheckProviders () {
        getPublicKey().checkProviders();

        Provider[] providers = Security.getProviders();
        assert (containsBouncyCastleProvider(providers));
    }

    // also tests encrypt(byte[]) decrypt(EncryptedMessage) and decrypt(byte[])
    public void testEncrypt () throws Exception {
        EncryptedMessage encryptedMessage = getPublicKey().encrypt(TEST_ALGORITHM, TEST_MESSAGE.getBytes());
        byte[] plainText = getPrivateKey().decrypt(encryptedMessage);
        String decryptedMessage = new String (plainText);

        assert (TEST_MESSAGE.equals(decryptedMessage));
    }

    // also tests decryptString

    public void testEncryptString () throws Exception {
        String cipertextString = getPublicKey().encryptString(TEST_MESSAGE);
        String plaintextString = getPrivateKey().decryptString(cipertextString);
        assert (plaintextString.equals(TEST_MESSAGE));
    }


    public void testToPem () throws Exception {
        String pem = getPublicKey().toPem();
        PublicKey publicKey = PublicKey.fromPEM(pem);
        assert (publicKey.equals(getPublicKey()));
    }
}
