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

package com.ltsllc.commons.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class HexConverter {
    private static final char[] chars = "0123456789ABCDEF".toCharArray();

    public static String toHexString (InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] buffer = Utils.readCompletely (inputStream);

        return toHexString(buffer);
    }

    public static String toHexString (byte[] byteArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : byteArray) {
            stringBuilder.append(toHexString(b));
        }

        return stringBuilder.toString();
    }

    public static String toHexString (int b) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(chars[toHighNibble(b)]);
        b = b & 0xF;
        stringBuilder.append(chars[b]);

        return stringBuilder.toString();
    }

    public static int toHighNibble(int i) {
        i = i & 0xF0;
        i = i >> 4;
        return i;
    }

    public static byte[] toByteArray (Reader reader)
            throws IOException
    {
        String string = Utils.readCompletely(reader);
        return toByteArray(string);
    }

    public static byte[] toByteArray (String string)
    {
        char[] charArray = string.toCharArray();
        if ((charArray.length % 2) != 0)
            throw new IllegalArgumentException("input string must have an even number of characters");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < charArray.length; i = i + 2)
        {
            int b = toByte(charArray, i);
            baos.write(b);
        }

        return baos.toByteArray();
    }

    public static int toByte (char[] charArray, int index)
    {
        int b = toNibble(charArray[index]);
        b = b << 4;
        return b + toNibble (charArray[index+1]);
    }

    public static int toNibble (char c) {
        c = Character.toUpperCase(c);
        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'A' && c <= 'F')
            return c - 'A' + 10;
        else
            throw new IllegalArgumentException("invalid hex character: " + c);
    }

}
