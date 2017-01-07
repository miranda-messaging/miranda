package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;

/**
 * Created by Clark on 1/5/2017.
 */
public class WriteMessage extends Message {
    private String filename;

    public String getFilename () {
        return filename;
    }

    public void setFilename (String s) {
        filename = s;
    }

    private byte[] buffer;

    public byte[] getBuffer () {
        return buffer;
    }

    public void setBuffer (byte[] b) {
        buffer = b;
    }

    public WriteMessage (String filename, byte[] buffer) {
        super(Subjects.Write);
        setBuffer(buffer);
        setFilename(filename);
    }
}
