package com.ltsllc.miranda.servlet;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;

/**
 * Created by Clark on 4/8/2017.
 */
public class StringServletInputStream extends ServletInputStream {
    private ByteArrayInputStream byteArrayInputStream;

    public ByteArrayInputStream getByteArrayInputStream() {
        return byteArrayInputStream;
    }

    public void setByteArrayInputStream(ByteArrayInputStream byteArrayInputStream) {
        this.byteArrayInputStream = byteArrayInputStream;
    }

    public StringServletInputStream (ByteArrayInputStream byteArrayInputStream) {
        this.byteArrayInputStream = byteArrayInputStream;
    }

    public int read () {
        return getByteArrayInputStream().read();
    }

    public boolean isReady () {
        return true;
    }

    public void setReadListener (ReadListener readListener) {}

    public boolean isFinished () {
        return false;
    }
}
