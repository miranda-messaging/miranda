package com.ltsllc.miranda.util;

import java.io.*;

/**
 * A collection of utity methods pertaining to I/O
 *
 * Created by Clark on 12/30/2016.
 */
public class IOUtils {
    /**
     * Close an {@link InputStream} --- ignoring any exceptions
     *
     * The routine only closes a non-null InputStream
     */
    public static void closeNoExceptions (InputStream in) {
        try{
            if (null != in) {
                in.close();
            }
        } catch (IOException e) {
            //
            // ignore the exception
            //
        }
    }

    public static void closeNoExceptions (OutputStream outputStream) {
        if (outputStream != null)
        {
            try {
                outputStream.close();
            } catch (IOException e) {
                //
                // ignore exceptions
                //
            }
        }
    }


    /**
     * Close a {@link Reader} --- ignoring any exceptions.
     *
     * The routine will only close a non-null Reader.
     */
    public static void closeNoExceptions (Reader r) {
        if (null != r) {
            try {
                r.close();
            } catch (IOException e) {
                //
                // ignore exceptions
                //
            }
        }
    }

    public static void closeNoExceptions (Writer out) {
        if (null != out) {
            try {
                out.close();
            } catch (IOException e) {
                //
                // ignore exceptions
                //
            }
        }
    }

}
