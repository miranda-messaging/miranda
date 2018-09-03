package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.messages.AuctionAbortMessage;
import com.ltsllc.miranda.cluster.messages.AuctionResultsMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;

import java.util.ArrayList;
import java.util.List;

public class AuctionOperationRemoteWaitingForResultState extends State {
    public AuctionOperationRemoteWaitingForResultState (AuctionOperation auctionOperation) {
        super(auctionOperation);
    }

    public AuctionOperation getAuction () {
        return (AuctionOperation) getContainer();
    }


    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getAuction().getCurrentState();

        switch (message.getSubject()) {
            case AuctionResults: {
                AuctionResultsMessage auctionResultsMessage = (AuctionResultsMessage) message;
                nextState = processAuctionResultsMessage(auctionResultsMessage);
                break;
            }

            case AuctionAbort: {
                AuctionAbortMessage auctionAbortMessage = (AuctionAbortMessage) message;
                nextState = processAuctionAbortMessage(auctionAbortMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processAuctionResultsMessage(AuctionResultsMessage message) {
        List<String> topics = new ArrayList<>();
        String host = Miranda.properties.getProperty(MirandaProperties.PROPERTY_MY_DNS);
        int port = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_MY_PORT);
        String me = host + ":" + port;
        Auction auction = message.getAuction();
        for (AuctionItem auctionItem : auction.getSubscriptionToAuctionItem().values()) {
            if (auctionItem.getBidder().equalsIgnoreCase(me))
                topics.add(auctionItem.getSubscription());
        }

        Miranda.getInstance().getSubscriptionManager().sendLocalSubscriptions(getAuction().getQueue(), getAuction(), topics);
        return StopState.getInstance();
    }

    public State processAuctionAbortMessage (AuctionAbortMessage auctionAbortMessage) {
        return StopState.getInstance();
    }
}
