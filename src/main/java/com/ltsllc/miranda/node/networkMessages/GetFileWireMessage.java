package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.clientinterface.requests.Files;

public class GetFileWireMessage {
    private Files file;

    public GetFileWireMessage (Files file) {
        setFile(file);
    }

    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }
}
