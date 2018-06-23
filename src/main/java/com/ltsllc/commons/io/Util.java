package com.ltsllc.commons.io;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static final int BUFFER_SIZE = 8192;

    public static byte[] readCompletely (InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bytesRead = inputStream.read(buffer);
        while (-1 != bytesRead) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
            bytesRead = inputStream.read(buffer);
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static void writeTextFile (String filename, String text) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        fileWriter.write(text);
        fileWriter.close();
    }
}