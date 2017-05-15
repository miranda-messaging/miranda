package com.ltsllc.miranda.servlet.encrypt;

import com.ltsllc.miranda.servlet.enctypt.CreateKeyPairServlet;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestCreateKeyPairServlet extends TestCase {
    private CreateKeyPairServlet servlet;

    public CreateKeyPairServlet getServlet() {
        return servlet;
    }

    public void reset () {
        super.reset();

        servlet = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        servlet = new CreateKeyPairServlet();
    }

    @Test
    public void testDoGet () {

    }
}
