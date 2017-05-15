package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.MirandaException;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.servlet.miranda.MirandaServlet;
import com.ltsllc.miranda.servlet.objects.ResultObject;
import com.ltsllc.miranda.servlet.user.UserObject;
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
