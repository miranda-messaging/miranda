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

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

/**
 * A message digest, using a cryptographically strong one way hashing algorithm
 */
public class MessageDigest {
    private java.security.MessageDigest jsMessageDigest;

    public MessageDigest () throws GeneralSecurityException {
        jsMessageDigest = java.security.MessageDigest.getInstance("SHA-256");;
    }

    public void update (byte[] bytes) {
        jsMessageDigest.update(bytes);
    }

    public void update (byte[] buffer, int offset, int length) {
        jsMessageDigest.update(buffer, offset, length);
    }

    public String asHexString () {
        byte[] digest = jsMessageDigest.digest();
        return HexConverter.toHexString(digest);
    }

    public static String calculate (byte[] array) throws GeneralSecurityException {
        MessageDigest messageDigest = new MessageDigest();
        messageDigest.update(array);
        return messageDigest.asHexString();
    }

    public static String calculate (InputStream inputStream) throws GeneralSecurityException, IOException {
        MessageDigest messageDigest = new MessageDigest();

        byte[] buffer = new byte[8192];
        int bytesRead = inputStream.read(buffer);
        while (bytesRead != -1) {
            messageDigest.update(buffer, 0, bytesRead);
        }

        return messageDigest.asHexString();
    }
}
