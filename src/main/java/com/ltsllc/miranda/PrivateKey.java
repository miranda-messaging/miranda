/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda;

import javax.crypto.Cipher;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/3/2017.
 */
public class PrivateKey extends Key {
    private java.security.PrivateKey securityPrivateKey;

    public java.security.PrivateKey getSecurityPrivateKey() {
        return securityPrivateKey;
    }

    public PrivateKey (java.security.PrivateKey privateKey) {
        securityPrivateKey = privateKey;
    }

    public byte[] encrypt(byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, securityPrivateKey);
        return cipher.doFinal(plainText);
    }

    public byte[] decrypt (byte[] cipherText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, securityPrivateKey);
        return cipher.doFinal(cipherText);
    }
}
