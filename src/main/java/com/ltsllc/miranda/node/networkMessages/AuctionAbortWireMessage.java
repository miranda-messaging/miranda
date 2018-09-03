package com.ltsllc.miranda.node.networkMessages;

public class AuctionAbortWireMessage extends WireMessage {
    public AuctionAbortWireMessage () {
        super(WireSubjects.AbortAuction);
    }
}
