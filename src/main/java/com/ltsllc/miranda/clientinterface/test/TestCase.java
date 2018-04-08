package com.ltsllc.miranda.clientinterface.test;

import com.ltsllc.commons.util.HexConverter;
import com.ltsllc.commons.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by ltsllc on 7/3/2017.
 */
public class TestCase extends com.ltsllc.miranda.test.TestCase {
    public boolean byteArraysAreEqual(byte[] a1, byte[] a2) {
        if (a1 == a2)
            return true;

        if (null == a1 || null == a2)
            return false;

        if (a1.length != a2.length)
            return false;

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i])
                return false;
        }

        return true;
    }


    public void reset() {
    }

    public void setup() {
    }

    public String fileToHexString(String filename) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        FileInputStream fileInputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            fileInputStream = new FileInputStream(filename);
            int b = fileInputStream.read();
            while (b != -1) {
                byteArrayOutputStream.write(b);
                b = fileInputStream.read();
            }

            fileInputStream.close();
            byteArrayOutputStream.close();

            return HexConverter.toHexString(byteArrayOutputStream.toByteArray());
        } finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }
    }
}
