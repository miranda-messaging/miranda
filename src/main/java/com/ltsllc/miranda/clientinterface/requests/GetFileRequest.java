package com.ltsllc.miranda.clientinterface.requests;

public class GetFileRequest extends Request {
    private Files file;
    private String guid;

    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public GetFileRequest (String sessionId, String guid, Files file) {
        super(sessionId);

        setGuid(guid);
        setFile(file);
    }

}
