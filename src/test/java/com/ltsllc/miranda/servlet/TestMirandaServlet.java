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

package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.objects.UserObject;
import com.ltsllc.miranda.clientinterface.results.ResultObject;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.test.TestServlet;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.mockito.Mockito.when;

/**
 * Created by Clark on 4/8/2017.
 */
public class TestMirandaServlet extends TestServlet {
    public static class TestServlet extends MirandaServlet {}
    private MirandaServlet mirandaServlet;

    public MirandaServlet getMirandaServlet() {
        return mirandaServlet;
    }

    public void reset () throws Exception {
        super.reset();

        mirandaServlet = null;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        mirandaServlet = new MirandaServlet();
    }

    @Test
    public void testReadSuccess () {
        String s = "hello, world!";

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());

        String result = null;

        try {
            result = getMirandaServlet().read(byteArrayInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert (result.equals(s));
    }

    @Test
    public void testReadException () {
        IOException ioException = new IOException("test");
        IOException result = null;

        try {
            when(getMockInputStream().read()).thenThrow(ioException);
            getMirandaServlet().read(getMockInputStream());
        } catch (IOException e) {
            result = e;
        }

        assert (null != result);
    }

    @Test
    public void testFromJsonSuccess () {
        String s = "{\"result\" : \"Success\"}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        ResultObject resultObject = null;

        try {
            resultObject = getMirandaServlet().fromJson(byteArrayInputStream, ResultObject.class);
        } catch (MirandaException e) {
            e.printStackTrace();
        }

        assert (null != resultObject);
        assert (resultObject.getResult().equals(Results.Success));
    }

    @Test
    public void testFromJsonIOException () {
        IOException ioException = new IOException("test");
        MirandaException result = null;

        try {
            when(getMockInputStream().read()).thenThrow(ioException);
            getMirandaServlet().fromJson(getMockInputStream(), ResultObject.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MirandaException e) {
            result = e;
        }

        assert (null != result);
    }

    @Test
    public void testFromJsonInvalidJson () {
        String json = "wrong";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(json.getBytes());
        MirandaException result = null;

        try {
            getMirandaServlet().fromJson(byteArrayInputStream, UserObject.class);
        } catch (MirandaException e) {
            result = e;
        }

        assert (null != result);
    }
}
