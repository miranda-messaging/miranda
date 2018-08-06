package com.ltsllc.commons.io;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.message.BasicHeader;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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


    public static Header[] getHeaders(HttpServletRequest httpServletRequest) {
        ArrayList<Header> list = new ArrayList<>();

        Enumeration<String> headerNameEnum = httpServletRequest.getHeaderNames();

        while (headerNameEnum.hasMoreElements())
        {
            String headerName = headerNameEnum.nextElement();
            String headerValue = httpServletRequest.getHeader(headerName);
            Header newHeader = new BasicHeader(headerName, headerValue);
            list.add(newHeader);
        }

        Header[] headers = new Header[list.size()];
        int index = 0;
        for (Header header : list) {
            headers[index] = list.get(index);
            index++;
        }

        return headers;
    }
}