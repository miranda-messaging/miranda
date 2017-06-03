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

import com.ltsllc.miranda.util.Utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.spec.KeySpec;

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

    public static final int RSA_BLOCK_SIZE = 245 - 11;

    public EncryptedMessage encrypt(byte[] plainText) throws GeneralSecurityException {
        throw new GeneralSecurityException("not implemented");
    }

    public byte[] encrypt (Cipher cipher, byte[] plainText, int blockSize) throws GeneralSecurityException {
        int numBlocks = calculateNumberOfBlocks(plainText.length, blockSize);
        byte[][] blocks = toBlocks(plainText, blockSize);

        byte[][] cipherTextBlocks = new byte[numBlocks][blockSize];

        for (int i = 0; i < (numBlocks - 1); i++) {
            cipherTextBlocks[i] = cipher.update(blocks[i]);
        }

        int finalIndex = blocks.length - 1;

        byte[] finalBlock = blocks[finalIndex];

        cipherTextBlocks[finalIndex] = cipher.doFinal(finalBlock);

        return toSingleBuffer(cipherTextBlocks, blockSize, numBlocks);
    }

    public byte[] decrypt (EncryptedMessage encryptedMessage) throws GeneralSecurityException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, getSecurityPrivateKey());
        byte[] encryptedKey = Utils.hexStringToBytes(encryptedMessage.getKey());
        byte[] plainTextKey = cipher.doFinal(encryptedKey);
        KeySpec keySpec = new SecretKeySpec(plainTextKey, "AES");
        cipher = Cipher.getInstance("AES");
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("AES");
        SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
        AlgorithmParameters algorithmParameters = cipher.getParameters();
        byte[] iv = algorithmParameters.getParameterSpec(IvParameterSpec.class).getIV();
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.generateKey();

        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] cipherTextMessage = Utils.hexStringToBytes(encryptedMessage.getMessage());
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cipherTextMessage);
        CipherInputStream cipherInputStream = new CipherInputStream(byteArrayInputStream, cipher);

        byte[] buffer = new byte[cipherTextMessage.length];
        cipherInputStream.read(buffer);

        return buffer;
    }
}
