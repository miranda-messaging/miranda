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

package com.ltsllc.commons.util;


import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.clcl.JavaKeyStore;
import com.ltsllc.miranda.property.MirandaProperties;

import java.io.*;
import java.net.Socket;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Clark on 2/3/2017.
 */
public class Utils {

    public static void closeIgnoreExceptions(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeIgnoreExceptions(Writer writer) {
        if (null != writer) {
            try {
                writer.close();
            } catch (IOException e) {
            }
        }
    }

    public static void closeIgnoreExceptions(OutputStream outputStream) {
        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(Socket socket) {
        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {

            }
        }
    }

    public static void closeIgnoreExceptions(Reader r) {
        if (null != r)
            try {
                r.close();
            } catch (IOException e) {

            }
    }

    public static String exceptionToString(Throwable throwable) {
        String result = null;

        if (throwable != null) {
            PrintWriter printWriter = null;
            try {
                StringWriter stringWriter = new StringWriter();
                printWriter = new PrintWriter(stringWriter);
                throwable.printStackTrace(printWriter);
                printWriter.close();
                result = stringWriter.toString();
            } finally {
                closeIgnoreExceptions(printWriter);
            }
        }

        return result;
    }

    public static String closeReturnExceptions(InputStream inputStream) {
        String messages = null;

        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                messages = "Exception closing input stream\n";
                messages += exceptionToString(e);
            }
        }

        return messages;
    }

    public static String closeReturnExceptions(OutputStream outputStream) {
        String messages = null;

        if (null != outputStream) {
            try {
                outputStream.close();
            } catch (IOException e) {
                messages = "Exception closing output stream\n";
                messages += exceptionToString(e);
            }
        }

        return messages;
    }

    public static String closeReturnExceptions(Socket socket) {
        String messages = null;

        if (null != socket) {
            try {
                socket.close();
            } catch (IOException e) {
                messages = "Exception trying to close socket";
                messages += exceptionToString(e);
            }
        }

        return messages;
    }

    public static String readInputStream(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = null;
        StringWriter stringWriter = null;

        try {
            stringWriter = new StringWriter();
            inputStreamReader = new InputStreamReader(inputStream);

            int c = inputStreamReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = inputStreamReader.read();
            }

            return stringWriter.toString();
        } finally {
            Utils.closeIgnoreExceptions(inputStreamReader);
            Utils.closeIgnoreExceptions(inputStream);
            Utils.closeIgnoreExceptions(stringWriter);
        }
    }

    public static String readAsString(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            throw new IOException("the file, " + filename + ", does not exist");
        }

        StringWriter stringWriter = new StringWriter();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);

            int c = fileReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = fileReader.read();
            }
        } finally {
            Utils.closeIgnoreExceptions(fileReader);
        }

        return stringWriter.toString();
    }

    public static String toStacktrace(Throwable t) {
        PrintWriter printWriter = null;
        try {
            StringWriter stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
            printWriter.close();
            return stringWriter.toString();
        } finally {
            closeIgnoreExceptions(printWriter);
        }
    }

    public static String readTextFile(String filename) throws IOException {
        StringWriter stringWriter = new StringWriter();
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filename);
            int c = fileReader.read();
            while (c != -1) {
                stringWriter.write(c);
                c = fileReader.read();
            }
        } finally {
            closeIgnoreExceptions(fileReader);
        }

        return stringWriter.toString();
    }

    public static void writeTextFile(String filename, String content) throws IOException {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename);
            fileWriter.write(content);
        } finally {
            closeIgnoreExceptions(fileWriter);
        }
    }

    /**
     * Read all the bytes from an {@link InputStream} and return an array of bytes that represent the value.
     *
     * @param inputStream The InputStream to read from.
     * @return the bytes read as an array
     * @throws IOException
     */
    public static byte[] readCompletely(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (inputStream != null) {
            for (int i = inputStream.read(); i != -1; i = inputStream.read()) {
                byteArrayOutputStream.write(i);
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Read all the characters in a Reader and return them as a String.
     *
     * @param reader
     * @return
     * @throws IOException
     */
    public static String readCompletely(Reader reader) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();

        if (reader != null) {
            for (int i = reader.read(); i != -1; i = reader.read()) {
                stringBuilder.append((char) i);
            }
        }

        return stringBuilder.toString();
    }

    public static List copy (List source) {
        List dest = new ArrayList(source.size());
        source.forEach(item -> dest.add(item));
        return dest;
    }

    public static KeyStore loadKeyStore(String filename, String password) throws EncryptionException {
        return JavaKeyStore.loadJsKeyStore(filename, password);
    }

    public static void backup(File file, String backupPrefix,  String backupSuffix, String backupExtension,
                              File backupDirectory) throws IOException {
        if (file.exists()) {
            File backup = new File(file.getCanonicalPath() + "." + backupExtension);
            if (backup.exists()) {
                backup = File.createTempFile(backupPrefix, backupSuffix, backupDirectory);
            }

            copyFile(file, backup);
        }
    }

    public static int BUFFER_SIZE = 8192;

    public static void copyFile (File srcfile, File dstfile) throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];

        InputStream inputStream = new FileInputStream(srcfile);
        OutputStream outputStream = new FileOutputStream(dstfile);

        int bytesread;
        do {
            bytesread = inputStream.read(buffer);
            outputStream.write(buffer, 0, bytesread);
        } while (bytesread >= BUFFER_SIZE);

        closeIgnoreExceptions(inputStream);
        closeReturnExceptions(outputStream);
    }


}
