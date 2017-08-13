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

package com.ltsllc.common.test;

import com.ltsllc.common.util.Utils;

import java.io.*;

public class TestCase {
    public String readTextFile (String filename) throws IOException {
        FileReader fileReader = null;

        try {
            StringWriter stringWriter = new StringWriter();
            fileReader = new FileReader(filename);
            int c = fileReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = fileReader.read();
            }

            return stringWriter.toString();
        } finally {
            Utils.closeIgnoreExceptions(fileReader);
        }
    }

    public void writeTextFile (String filename, String content) throws IOException {
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(content);
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }
    }

    public String fileToString (String filename) throws IOException {
        FileInputStream fileInputStream = null;

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(filename);
            int b = fileInputStream.read();
            while (b != -1) {
                byteArrayOutputStream.write(b);
                b = fileInputStream.read();
            }

            byteArrayOutputStream.close();
            return Utils.bytesToString(byteArrayOutputStream.toByteArray());
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }
    }

    public String differ (String s1, String s2) {
        if (s1 == s2 || s1.equals(s2)) {
            return null;
        }

        if (s1.length() != s2.length()) {
            String message = "String are of diferent lengths (" + s1.length() + " vs " + s2.length() + ")";
            return message;
        }

        for (int i = 1; i < s1.length(); i++) {
            String substring = s1.substring(0, i);
            if (s2.indexOf(substring) != 0) {
                String s1String = s1.substring(i - 4, i + 4);
                s1String = shorten (s1String);
                String s2String = s2.substring(i - 4, i + 4);
                s2String = shorten (s2String);
                String message = "Strings differ at index " + i;
                message = message + ", \"" + s1String + "\"";
                message = message + " vs \"" + s2String + "\"";

                return message;
            }
        }

        return null;
    }

    public String shorten (String string) {
        if (string.length() < 8)
            return string;

        String newString = string.substring(0, 7);
        newString = newString + "...";
        return newString;
    }

    public static void delete (String filename) {
        if (filename == null)
            return;

        File file = new File(filename);
        if (!file.exists())
            return;

        if (!file.delete()) {
            String message = "Could not delete " + file.getName();
            System.err.println(message);
        }
    }
}
