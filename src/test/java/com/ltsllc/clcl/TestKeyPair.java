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

public class TestKeyPair extends TestCase {
    public static final String TEST_PASSWORD = "whatever";

    private void createKeyPair() {
    }

    private KeyPair getKeyPair() {
        return null;
    }

    @Before
    public void setup () throws EncryptionException {
        createKeyPair();
    }


    public void testNewKeys () {
        assert (null != getKeyPair());
    }

    // also tests fromPem

    public void testToPemNoPassword () throws EncryptionException{
        String pem = getKeyPair().toPem();
        KeyPair keyPair = KeyPair.fromPem(pem);
        assert (getKeyPair().equals(keyPair));
    }


    public void testToPemWithPassword () throws EncryptionException {
        String pem = getKeyPair().toPem(TEST_PASSWORD);
        KeyPair keyPair = KeyPair.fromPem(pem, TEST_PASSWORD);
        assert (getKeyPair().equals(keyPair));
    }
}
