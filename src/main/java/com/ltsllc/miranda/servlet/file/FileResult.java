package com.ltsllc.miranda.servlet.file;

import com.ltsllc.miranda.servlet.objects.ResultObject;

/**
 * Created by Clark on 4/18/2017.
 */
public class FileResult extends ResultObject {
    private byte[] content;

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
