package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.miranda.Miranda;

public class AuctionOperationStartState extends State {
    public AuctionOperationStartState(AuctionOperation operation) {
        super(operation);
    }

    public AuctionOperation getOperation () {
        return (AuctionOperation) container;
    }

    @Override
    public State start() {
        Miranda.getInstance().getSubscriptionManager().sendCreateBid(getOperation().getQueue(), getOperation());
        AuctionOperationPreparingBidState auctionPreparingBidState = new AuctionOperationPreparingBidState();
        return auctionPreparingBidState;
    }
}
