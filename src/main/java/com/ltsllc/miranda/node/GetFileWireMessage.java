package com.ltsllc.miranda.node;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileWireMessage extends WireMessage {
    private String file;

    public GetFileWireMessage (String file) {
        super(WireSubjects.GetFile);

        this.file = file;
    }

    public String getFile() {
        return file;
    }
}
