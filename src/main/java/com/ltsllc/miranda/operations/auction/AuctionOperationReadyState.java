package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;

/**
 * Created by Clark on 4/22/2017.
 */
public class AuctionOperationReadyState extends State {
    public AuctionOperation getAuctionOperation () {
        return (AuctionOperation) getContainer();
    }

    public AuctionOperationReadyState (AuctionOperation auctionOperation) {
        super(auctionOperation);
    }
}
