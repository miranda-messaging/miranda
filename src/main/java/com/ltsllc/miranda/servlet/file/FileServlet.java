package com.ltsllc.miranda.servlet.file;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.MirandaServlet;
import org.eclipse.jetty.server.Request;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Clark on 4/18/2017.
 */
public class FileServlet extends MirandaServlet {
    public byte[] readInputStream (InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int c = inputStream.read();
        while (c != -1) {
            byteArrayOutputStream.write(c);
            c = inputStream.read();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public void copy (InputStream inputStream, ServletOutputStream outputStream) throws IOException {
        int c = inputStream.read();
        while (c != -1) {
            outputStream.write(c);
            c = inputStream.read();
        }
    }

    public static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MULTI_PART_CONFIG);
        FileResult fileResult = new FileResult();
        Part part = req.getPart("content");
        fileResult.setContent(readInputStream(part.getInputStream()));

        fileResult.setResult(Results.Success);

        respond(resp.getOutputStream(), fileResult);
        resp.setStatus(200);
    }
}
