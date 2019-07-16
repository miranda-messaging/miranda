package com.ltsllc.miranda.operations.syncfiles.messages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

public class GetVersionsWireMessage extends WireMessage {
    public GetVersionsWireMessage() {
        super(WireSubjects.GetVersions);
    }

}
