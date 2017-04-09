package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.objects.UserObject;
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
    private MirandaServlet mirandaServlet;

    public MirandaServlet getMirandaServlet() {
        return mirandaServlet;
    }

    public void reset () {
        super.reset();

        mirandaServlet = null;
    }

    @Before
    public void setup () {
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
        String s = "{\"result\" : \"success\"}";
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
        ResultObject resultObject = null;

        try {
            resultObject = getMirandaServlet().fromJson(byteArrayInputStream, ResultObject.class);
        } catch (MirandaException e) {
            e.printStackTrace();
        }

        assert (null != resultObject);
        assert (resultObject.getResult().equals("success"));
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
