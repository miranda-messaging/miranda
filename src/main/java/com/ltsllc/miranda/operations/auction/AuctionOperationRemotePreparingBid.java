package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.topics.messages.CreateBidResponseMessage;

/**
 * A state that indicates someone has remotely triggered an auction
 */
public class AuctionOperationRemotePreparingBid extends State {
    public AuctionOperation getAuction () {
        return (AuctionOperation) getContainer();
    }

    public AuctionOperationRemotePreparingBid(AuctionOperation auctionOperation)
    {
        super(auctionOperation);
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getAuction().getCurrentState();

        switch (message.getSubject()) {
            case CreateBidResponse: {
                CreateBidResponseMessage createBidResponseMessage = (CreateBidResponseMessage) message;
                nextState = processBidResponseMessage(createBidResponseMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processBidResponseMessage(CreateBidResponseMessage createBidResponseMessage) {
        String host = Miranda.properties.getProperty(MirandaProperties.PROPERTY_MY_DNS);
        int port = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_MY_PORT);

        getAuction().recordBid(createBidResponseMessage.getBid());
        AuctionOperationRemoteWaitingForResultState nextState = new AuctionOperationRemoteWaitingForResultState();

        long timeout = Miranda.properties.getLongProperty(MirandaProperties.PROPERTY_AUCTION_TIMEOUT,
                MirandaProperties.DEFAULT_AUCTION_TIMEOUT);
        AuctionTimeoutMessage auctionTimeoutMessage = new AuctionTimeoutMessage();
        Miranda.timer.sendScheduleOnce(new Long(timeout), getAuction().getQueue(), auctionTimeoutMessage);
        return nextState;
    }
}
