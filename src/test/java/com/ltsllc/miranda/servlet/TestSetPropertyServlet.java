package com.ltsllc.miranda.servlet;

import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.servlet.property.SetPropertyServlet;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by Clark on 3/24/2017.
 */
public class TestSetPropertyServlet extends TestCase {
    @Mock
    private HttpServletRequest mockServletRequest;

    @Mock
    private HttpServletResponse mockServletResponse;

    private SetPropertyServlet setPropertyServlet;

    public SetPropertyServlet getSetPropertyServlet() {
        return setPropertyServlet;
    }

    public HttpServletResponse getMockServletResponse() {
        return mockServletResponse;
    }

    public HttpServletRequest getMockServletRequest() {

        return mockServletRequest;
    }

    public void reset () {
        super.reset();

        mockServletRequest = null;
        mockServletResponse = null;
        setPropertyServlet = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        setuplog4j();

        mockServletRequest = mock(HttpServletRequest.class);
        mockServletResponse = mock(HttpServletResponse.class);
        setPropertyServlet = new SetPropertyServlet();
    }

    @Test
    public void testDoPost () {
        setupMockProperties();
        List<String> names = new ArrayList<String>();
        names.add(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS);
        Enumeration<String> listEnumeration = Collections.enumeration(names);

        when(getMockServletRequest().getParameterNames()).thenReturn(listEnumeration);
        when(getMockServletRequest().getParameter(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS)).thenReturn("whatever");
        when(getMockProperties().getProperty(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS)).thenReturn(MirandaProperties.DEFAULT_CERTIFICATE_ALIAS);

        try {
            getSetPropertyServlet().doPost(getMockServletRequest(), getMockServletResponse());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        verify(getMockProperties(), atLeastOnce()).setProperty(Matchers.eq(MirandaProperties.PROPERTY_CERTIFICATE_ALIAS),
                Matchers.eq("whatever"));
    }
}
