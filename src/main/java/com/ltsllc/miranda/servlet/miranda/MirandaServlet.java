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

package com.ltsllc.miranda.servlet.miranda;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.user.JSPublicKeySerializer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Created by Clark on 4/7/2017.
 */
public class MirandaServlet extends HttpServlet {
    public void doOptions (HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Allow", "*");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, Access-Control-Allow-Origin");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Access-Control-Max-Age", "1209600");
    }


    public boolean allowAccess () {
        return true;
    }

    public static final String LOGIN_PAGE = "/login.html";

    private static Gson gson = createGson();

    public static Gson createGson () {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(java.security.PublicKey.class, new JSPublicKeySerializer());

        return gsonBuilder.create();
    }

    public static Gson getGson() {
        return gson;
    }

    public String read(InputStream inputStream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        StringWriter stringWriter = new StringWriter();
        for (int c = inputStreamReader.read(); c != -1; c = inputStreamReader.read()) {
            stringWriter.append((char) c);
        }

        return stringWriter.toString();
    }

    public String inputStreamToString (InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int b = inputStream.read();
        while (-1 != b) {
            byteArrayOutputStream.write(b);
            b = inputStream.read();
        }

        String s = new String(byteArrayOutputStream.toByteArray());
        return s;
    }

    public <T> T fromJson(InputStream inputStream, Class<T> type) throws MirandaException {
        try {
            String json = inputStreamToString(inputStream);
            return gson.fromJson(json, type);
        } catch (IOException | JsonSyntaxException e) {
            throw new MirandaException("Exception trying to get object", e);
        }
    }

    public void respond(ServletOutputStream output, Object o) throws IOException {
        String json = gson.toJson(o);
        output.println(json);
    }
}
