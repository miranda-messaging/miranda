package com.ltsllc.miranda.servlet;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ltsllc.miranda.MirandaException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by Clark on 4/7/2017.
 */
public class MirandaServlet extends HttpServlet {
    private Gson gson = new Gson();

    public String read(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringWriter stringWriter = new StringWriter();
        for (int c = inputStreamReader.read(); c != -1; c = inputStreamReader.read()) {
            stringWriter.append((char) c);
        }

        return stringWriter.toString();
    }

    public <T> T fromJson (InputStream inputStream, Class<T> type) throws MirandaException {
        try {
            String json = read(inputStream);
            return gson.fromJson(json, type);
        } catch (IOException | JsonSyntaxException e) {
            throw new MirandaException("Exception trying to get object", e);
        }
    }

    public void respond (ServletOutputStream output, Object o) throws IOException {
        String json = gson.toJson(o);
        output.println(json);
    }
}
