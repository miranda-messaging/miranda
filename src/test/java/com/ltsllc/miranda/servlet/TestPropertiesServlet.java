package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.property.PropertiesServlet;
import com.ltsllc.miranda.test.TestCase;
import com.ltsllc.miranda.util.PropertiesUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestPropertiesServlet extends TestCase {
    @Mock
    private HttpServletRequest mockServletRequest;

    @Mock
    private HttpServletResponse mockServletResponse;

    private PropertiesServlet propertiesServlet;

    public HttpServletResponse getMockServletResponse() {
        return mockServletResponse;
    }

    public HttpServletRequest getMockServletRequest() {

        return mockServletRequest;
    }

    public PropertiesServlet getPropertiesServlet() {
        return propertiesServlet;
    }

    public void reset () {
        super.reset();

        propertiesServlet = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        mockServletRequest = mock(HttpServletRequest.class);
        mockServletResponse = mock(HttpServletResponse.class);
        propertiesServlet = new PropertiesServlet();
    }

    @Test
    public void testDoGet () {
        setupMockProperties();
        Properties p = PropertiesUtils.buildFrom(MirandaProperties.DEFAULT_PROPERTIES);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        try {
            when(getMockServletResponse().getWriter()).thenReturn(printWriter);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        when(getMockProperties().asProperties()).thenReturn(p);

        try {
            getPropertiesServlet().doGet(getMockServletRequest(), getMockServletResponse());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        verify(getMockServletResponse(), atLeastOnce()).setContentType(Matchers.eq("text/json"));
        verify(getMockServletResponse(), atLeastOnce()).setStatus(Matchers.eq(HttpServletResponse.SC_OK));

        printWriter.close();
    }


}
