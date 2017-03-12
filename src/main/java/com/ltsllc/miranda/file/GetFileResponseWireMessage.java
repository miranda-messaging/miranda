package com.ltsllc.miranda.file;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * Created by Clark on 2/11/2017.
 */
public class GetFileResponseWireMessage extends WireMessage {
    private String contents;
    private String requester;

    public GetFileResponseWireMessage (String requester, String contents) {
        super(WireSubjects.GetFileResponse);

        this.contents = contents;
        this.requester = requester;
    }

    public String getContents() {
        return contents;
    }

    public String getRequester() {
        return requester;
    }
}
