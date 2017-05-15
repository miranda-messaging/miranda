package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.Consumer;

/**
 * Created by Clark on 4/22/2017.
 */
public class AuctionOperation extends Consumer {
    public AuctionOperation () {
        super("auction operation");
        AuctionOperationReadyState auctionOperationReadyState = new AuctionOperationReadyState(this);
        setCurrentState(auctionOperationReadyState);
    }
}
