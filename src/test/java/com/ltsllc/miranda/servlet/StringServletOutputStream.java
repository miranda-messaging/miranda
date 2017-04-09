package com.ltsllc.miranda.servlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.OutputStream;
import java.io.StringWriter;

/**
 * Created by Clark on 4/8/2017.
 */
public class StringServletOutputStream extends ServletOutputStream {
    private StringWriter stringWriter;

    public StringWriter getStringWriter() {
        return stringWriter;
    }

    public StringServletOutputStream () {
        this.stringWriter = new StringWriter();
    }

    public void write (int c)
    {
        getStringWriter().write(c);
    }

    public void setWriteListener (WriteListener writeListener)
    {
    }

    public boolean isReady () {
        return true;
    }
}
