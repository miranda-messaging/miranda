package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.clientinterface.requests.Files;

public class GetFileWireMessage extends WireMessage{
    private Files file;

    public GetFileWireMessage (Files file) {
        super(WireSubjects.GetFile);
        setFile(file);
    }

    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
