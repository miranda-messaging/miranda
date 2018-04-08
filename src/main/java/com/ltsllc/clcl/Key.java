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
import com.ltsllc.commons.util.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Provider;
import java.security.Security;

/**
 * A class that can be used to encrypt or decrypt messages.
 *
 * <p>
 *     This class provides utility methods for its subclasses like the {@link #encrypt(String, byte[])}
 *     and {@link #decrypt(EncryptedMessage)} methods.
 * </p>
 */
abstract public class Key implements Serializable {
    abstract public String getSessionAlgorithm ();
    abstract public byte[] encrypt (byte[] plainText) throws EncryptionException;
    abstract public byte[] decrypt (byte[] cipherText) throws EncryptionException;
    abstract public String toPem () throws EncryptionException;

    public static String SESSION_ALGORITHM = "AES";

    private DistinguishedName dn;

    public DistinguishedName getDn() {
        return dn;
    }

    public void setDn(DistinguishedName dn) {
        this.dn = dn;
    }

    public EncryptedMessage encryptToMessage (byte[] plainText) throws EncryptionException {
        return encrypt(getSessionAlgorithm(), plainText);
    }

    public byte[] decryptFromMessage (EncryptedMessage encryptedMessage) throws EncryptionException {
        return decrypt(encryptedMessage);
    }

    /**
     * Encrypt a message.
     *
     * <p>
     *     This is a utility method for encrypting messages.
     *     It encrypts messages with a "fast" algorithm and the session key used is encrypted
     *     with the object to provide security.
     * </p>
     *
     * @param algorithm The algorithm that should be used to encrypt the message.
     * @param plainText The message to be encrypted.
     * @return An object containing the encrypted message and the encrypted session key.
     */
    public EncryptedMessage encrypt (String algorithm, byte[] plainText)
            throws EncryptionException
    {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            SecretKey sessionKey = keyGenerator.generateKey();

            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, sessionKey);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
            cipherOutputStream.write(plainText);
            cipherOutputStream.close();

            byte[] sessionKeyCipherText = encrypt(sessionKey.getEncoded());
            String sessionKeyCipherTextString = HexConverter.toHexString(sessionKeyCipherText);
            String cipherTextString = HexConverter.toHexString(byteArrayOutputStream.toByteArray());

            EncryptedMessage encryptedMessage = new EncryptedMessage(algorithm, sessionKeyCipherTextString, cipherTextString);

            return encryptedMessage;
        } catch (GeneralSecurityException|IOException e) {
            throw new EncryptionException("Exception trying to encrypt message", e);
        }
    }

    /**
     * A convenience method for encrypting Strings.
     *
     * <p>
     *     This method calls {@link #encrypt(byte[])} under the covers.
     * </p>
     *
     * @param plainTextString The string to be encrypted.
     * @return A hexadecimal string (suitable for use with {@link HexConverter#toByteArray(String)} that is the
     * input encrypted.
     * @throws EncryptionException If there is a problem encrypting.
     */
    public String encryptString (String plainTextString) throws EncryptionException {
        byte[] plainText = plainTextString.getBytes();
        byte[] cipherText = encrypt(plainText);
        return HexConverter.toHexString(cipherText);
    }

    /**
     * A convenience method for decrypting strings.
     *
     * <p>
     *     This method assumes that the input is a hexadecimal string that represents the encrypted string.
     *     This method calls {@link HexConverter#toByteArray(String)} to convert the input to bytes before decrypting it.
     * </p>
     *
     * @param hexString A hexadecimal string that represents the encrypted string.
     * @return The decrypted string.
     * @throws IOException If there is a problem converting the input string to bytes.
     * @throws EncryptionException If there is a problem decrypting the string.
     * @return The decrypted string.
     */
    public String decryptString (String hexString) throws IOException, EncryptionException {
        byte[] cipherText = HexConverter.toByteArray(hexString);
        byte[] plainText = decrypt(cipherText);
        return new String(plainText);
    }

    /**
     * Decrypt a message.
     *
     * <p>
     *     This is a utility method for decrypting messages.
     *     A message is encrypted with a "fast" algorithm whose key is part of the message object.
     * </p>
     *
     * @param encryptedMessage The message to be decrypted.
     * @return The decrypted message.
     * @throws EncryptionException If there is a problem decrypting the message.
     */
    public byte[] decrypt (EncryptedMessage encryptedMessage)
        throws EncryptionException
    {
        try {
            checkProviders();

            byte[] sessionKeyPlainText = decrypt(HexConverter.toByteArray(encryptedMessage.getKey()));
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(encryptedMessage.getAlgorithm());

            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyPlainText, encryptedMessage.getAlgorithm());
            SecretKey sessionKey = secretKeyFactory.generateSecret(secretKeySpec);

            Cipher cipher = Cipher.getInstance(encryptedMessage.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, sessionKey);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            CipherOutputStream cipherOutputStream = new CipherOutputStream(byteArrayOutputStream, cipher);
            cipherOutputStream.write(HexConverter.toByteArray(encryptedMessage.getMessage()));
            cipherOutputStream.close();

            return byteArrayOutputStream.toByteArray();
        } catch (GeneralSecurityException|IOException e) {
            throw new EncryptionException("Exception trying to decrypt message", e);
        }
    }

    /**
     * Make sure the {@link BouncyCastleProvider} is available.
     *
     * <p>
     *     This method uses {@link Security#getProviders()} to determine if the BouncyCastleProvider is present.
     *     If is missing then the method adds it with {@link Security#addProvider(Provider)}.
     * </p>
     */
    public void checkProviders () {
        Provider[] providers = Security.getProviders();

        boolean hasBouncyCastle = false;

        for (Provider provider : providers) {
            if (provider instanceof BouncyCastleProvider) {
                hasBouncyCastle = true;
            }
        }

        if (!hasBouncyCastle)
            Security.addProvider(new BouncyCastleProvider());
    }
}
