package com.ltsllc.miranda.cluster.networkMessages;

import com.ltsllc.miranda.node.networkMessages.WireMessage;

/**
 * This message asks a Node for all it's versions
 */
public class GetVersionWireMessage extends WireMessage {
    public GetVersionWireMessage() {
        super(WireSubjects.GetVersions);
    }
}
