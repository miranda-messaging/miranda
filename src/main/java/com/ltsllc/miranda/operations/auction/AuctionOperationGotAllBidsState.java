package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;

import java.util.ArrayList;
import java.util.List;
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
        Miranda.timer.cancel(getAuction().getQueue());

        String host = Miranda.properties.getProperty(MirandaProperties.PROPERTY_MY_DNS);
        int port = Miranda.properties.getIntProperty(MirandaProperties.PROPERTY_MY_PORT);
        String me = host + ":" + port;
        List<String> localSubscriptions = new ArrayList<>();

        for (AuctionItem auctionItem : getAuction().getAuction().getSubscriptionToAuctionItem().values())
        {
            if (auctionItem.getBidder().equals(me)) {
                localSubscriptions.add(auctionItem.getSubscription());
            }
        }
        Miranda.getInstance().getSubscriptionManager().sendLocalSubscriptions(getAuction().getQueue(), getAuction(),
                localSubscriptions);

        return StopState.getInstance();
    }

  }
