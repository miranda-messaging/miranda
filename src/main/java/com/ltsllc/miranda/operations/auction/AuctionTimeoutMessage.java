package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.message.Message;

/**
 * An auction has timed out
 */
public class AuctionTimeoutMessage extends Message {
    public AuctionTimeoutMessage()
    {
        super(Subjects.AuctionTimeout, null, null);
    }

}
