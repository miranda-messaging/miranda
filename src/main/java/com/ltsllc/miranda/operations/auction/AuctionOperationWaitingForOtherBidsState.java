package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.topics.messages.CreateBidResponseMessage;
import org.apache.log4j.Logger;

public class AuctionOperationWaitingForOtherBidsState extends State {
    private static Logger LOGGER = Logger.getLogger(AuctionOperationWaitingForOtherBidsState.class);

    private int numberOfOthers;

    public int getNumberOfOthers() {
        return numberOfOthers;
    }

    public void setNumberOfOthers(int numberOfOthers) {
        this.numberOfOthers = numberOfOthers;
    }

    public AuctionOperation getAuction() {
        return (AuctionOperation) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getAuction().getCurrentState();

        switch (message.getSubject()) {
            case CreateBidResponse: {
                CreateBidResponseMessage createBidResponseMessage = (CreateBidResponseMessage) message;
                nextState = processCreateBidResponseMessage(createBidResponseMessage);
                break;
            }

            case Bid: {
                BidMessage bidMessage = (BidMessage) message;
                nextState = processBidMessage(bidMessage);
                break;
            }

            case AuctionTimeout: {
                AuctionTimeoutMessage auctionTimeoutMessage = (AuctionTimeoutMessage) message;
                nextState = processAuctionTimeoutMessage (auctionTimeoutMessage);
                break;
            }


            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateBidResponseMessage(CreateBidResponseMessage message) {
        if (message.getBid() != null) {
            getAuction().recordBid(message.getBid());
            Miranda.getInstance().getCluster().sendAuction(getAuction().getQueue(), getAuction(), message.getBid());
            Miranda.getInstance().getCluster().sendGetNodeCount(getAuction().getQueue(), getAuction());
            return getAuction().getCurrentState();
        } else {
            LOGGER.error("Could not create bid.  Auction aborted.");
            StopState stopState = StopState.getInstance();
            return stopState;
        }
    }

    public State processBidMessage(BidMessage bidMessage) {
        decrementNumberOfOthers();
        if (getNumberOfOthers() < 1) {
            AuctionOperationGotAllBidsState auctionOperationGotAllBids = new AuctionOperationGotAllBidsState(getAuction());
            return auctionOperationGotAllBids;
        }
        else
            return getAuction().getCurrentState();
    }

    public void decrementNumberOfOthers() {
        numberOfOthers--;
    }

    public State processAuctionTimeoutMessage (AuctionTimeoutMessage auctionTimeoutMessage) {
        LOGGER.error ("The auction timed out, aborting auction");
        Miranda.getInstance().getCluster().sendAuctionAbort (getAuction().getQueue(), getAuction());
        StopState stopState = StopState.getInstance();
        return stopState;
    }
}