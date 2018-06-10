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

package com.ltsllc.miranda.servlet.file;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created by Clark on 4/18/2017.
 */
public class FileServlet extends MirandaServlet {
    public byte[] readInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int c = inputStream.read();
        while (c != -1) {
            byteArrayOutputStream.write(c);
            c = inputStream.read();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public void copy(InputStream inputStream, ServletOutputStream outputStream) throws IOException {
        int c = inputStream.read();
        while (c != -1) {
            outputStream.write(c);
            c = inputStream.read();
        }
    }

    public static final MultipartConfigElement MULTI_PART_CONFIG = new MultipartConfigElement(System.getProperty("java.io.tmpdir"));

    public Method[] getAllDeclaredMethods(Class<? extends HttpServlet> c) {
        Class<?> clazz = c;

        Method[] allMethods;
        for (allMethods = null; !clazz.equals(HttpServlet.class); clazz = clazz.getSuperclass()) {
            Method[] thisMethods = clazz.getDeclaredMethods();
            if (allMethods != null && allMethods.length > 0) {
                Method[] subClassMethods = allMethods;
                allMethods = new Method[thisMethods.length + allMethods.length];
                System.arraycopy(thisMethods, 0, allMethods, 0, thisMethods.length);
                System.arraycopy(subClassMethods, 0, allMethods, thisMethods.length, subClassMethods.length);
            } else {
                allMethods = thisMethods;
            }
        }

        return allMethods != null ? allMethods : new Method[0];
    }
}
