package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.AuctionMessage;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.topics.messages.CreateBidResponseMessage;

public class AuctionOperationPreparingBidState extends State {
    public AuctionOperation getAuction () {
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

            case Auction: {
                AuctionMessage auctionMessage = (AuctionMessage) message;
                nextState = processAuctionMessage(auctionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processCreateBidResponseMessage (CreateBidResponseMessage message) {
        String host = Miranda.properties.getProperty(MirandaProperties.PROPERTY_MY_DNS);
        int port = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_MY_PORT);

        getAuction().recordBid(message.getBid());
        AuctionOperationWaitingForOtherBidsState auctionOperationWaitingForOtherBidsState = new AuctionOperationWaitingForOtherBidsState();

        long timeout = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_AUCTION_TIMEOUT,
                MirandaProperties.DEFAULT_AUCTION_TIMEOUT);
        AuctionTimeoutMessage auctionTimeoutMessage = new AuctionTimeoutMessage();
        Miranda.timer.sendScheduleOnce(new Long(timeout), getAuction().getQueue(), auctionTimeoutMessage);
        return auctionOperationWaitingForOtherBidsState;
    }

    public State processAuctionMessage (AuctionMessage auctionMessage) {
        Auction auction = new Auction();
        auction.recordBid(auctionMessage.getBid());

        AuctionOperationRemotePreparingBid auctionOperationRemotePrepairingBid = new AuctionOperationRemotePreparingBid();
        return  auctionOperationRemotePrepairingBid;
    }
}
