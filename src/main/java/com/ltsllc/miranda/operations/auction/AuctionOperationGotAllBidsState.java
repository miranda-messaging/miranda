package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;

import java.util.concurrent.BlockingQueue;

public class AuctionOperationGotAllBidsState extends State {
    public AuctionOperationGotAllBidsState(AuctionOperation operation) {
        super(operation);
    }

    public AuctionOperation getAuction () {
        return (AuctionOperation) getContainer();
    }

    public State start () {
        Miranda.getInstance().getCluster().sendAuctionResults(getAuction().getAuction(), getAuction().getQueue(), getAuction());
        return this;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getAuction().getCurrentState();

        switch (message.getSubject())
        {
            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return super.processMessage(message);
    }
}
