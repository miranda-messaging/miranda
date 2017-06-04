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

import com.google.gson.Gson;
import com.ltsllc.miranda.util.Utils;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 4/2/2017.
 */
public class PublicKey extends Key {
    private static Gson gson = new Gson();

    private java.security.PublicKey securityPublicKey;

    public java.security.PublicKey getSecurityPublicKey() {
        return securityPublicKey;
    }

    public PublicKey (java.security.PublicKey publicKey) {
        this.securityPublicKey = publicKey;
    }

    public static final int RSA_BLOCK_SIZE = 245;

    public EncryptedMessage encrypt(byte[] plainText) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, getSecurityPublicKey());
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encryptedKey = cipher.doFinal(secretKey.getEncoded());
        String encryptedKeyHexString = Utils.bytesToString(encryptedKey);

        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);

        try {
            cipherOutputStream.write(plainText);
            cipherOutputStream.close();
        } catch (IOException e) {
            Panic panic = new Panic("Exception encrypting data", e, Panic.Reasons.ExceptionEncrypting);
        }

        byte[] cipherText = byteArrayOutputStream.toByteArray();
        String cipherTextString = Utils.bytesToString(cipherText);
        EncryptedMessage encryptedMessage = new EncryptedMessage();
        encryptedMessage.setKey(encryptedKeyHexString);
        encryptedMessage.setMessage(cipherTextString);

        String json = gson.toJson(encryptedMessage);



        return encryptedMessage;
    }

    public byte[] encrypt (Cipher cipher, SecretKey secretKey, byte[] plainText) throws GeneralSecurityException {
        byte[][] blocks = toBlocks(plainText, cipher.getBlockSize());
        byte[][] cipherText = encrypt (cipher, blocks);
        return toSingleBuffer(cipherText);
    }

    public byte[] toSingleBuffer (byte[][] source) {
        int totalSize = 0;

        for (int i = 0; i < source.length; i++) {
            totalSize += source[i].length;
        }

        byte[] destination = new byte[totalSize];

        int offset = 0;
        for (int i = 0; i < source.length; i++) {
            byte[] current = source[i];
            copyFromBlock(current, destination, offset);
            offset += current.length;
        }

        return destination;
    }

    public byte[][] encrypt (Cipher cipher, byte[][] plainText) throws GeneralSecurityException  {
        byte[][] cipherText = new byte[plainText.length][];

        for (int i = 0; i < (plainText.length - 1); i++) {
            cipherText[i] = cipher.update(plainText[i]);
        }

        int finalIndex = plainText.length - 1;

        cipherText[finalIndex] = cipher.doFinal(plainText[finalIndex]);

        return cipherText;
    }


    public byte[] copyBlock (byte[] source) {
        byte[] destination = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            destination[i] = source[i];
        }

        return destination;
    }

    public byte[] toSingleBlock (byte[][] source) {
        int totalSize = 0;
        for (int i = 0; i < source.length; i++) {
            totalSize += source[i].length;
        }

        byte[] destination = new byte[totalSize];

        int index = 0;
        for (int i = 0; i < source.length; i++) {
            byte[] current = source[i];
            copyFromBlock (current, destination, index);
            index += current.length;
        }

        return destination;
    }

    public void copyFromBlock (byte[] source, byte[] destination, int destinationOffset) {
        for (int i = 0; i < source.length; i++) {
            destination[i + destinationOffset] = source[i];
        }
    }

    public byte[] decrypt (EncryptedMessage encryptedMessage) throws GeneralSecurityException {
        throw new GeneralSecurityException("not implemented");
    }
}
