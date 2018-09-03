package com.ltsllc.miranda.node.networkMessages;

import com.ltsllc.miranda.operations.auction.Bid;

/**
 * A physical message indicating an auction
 */
public class AuctionWireMessage extends WireMessage {
    private Bid bid;

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public AuctionWireMessage(Bid bid) {
        super(WireSubjects.Auction);
        setBid(bid);
    }
}
