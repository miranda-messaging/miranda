package com.ltsllc.miranda.operations.auction;

import java.util.Map;

/**
 * A redistribution of topic
 */
public class Auction {
    private Map<String, AuctionItem>  subscriptionToAuctionItem;

    public Map<String, AuctionItem> getSubscriptionToAuctionItem() {
        return subscriptionToAuctionItem;
    }

    public void setSubscriptionToAuctionItem(Map<String, AuctionItem> topicToAuctionItem) {
        this.subscriptionToAuctionItem = subscriptionToAuctionItem;
    }

    public void recordBid (Bid bid) {
        for (String subscription : bid.getBids().keySet()) {
            if (subscriptionToAuctionItem.get(subscription).getBid() < bid.getBidFor(subscription)) {
                AuctionItem auctionItem = subscriptionToAuctionItem.get(subscription);
                if (null == auctionItem) {
                    auctionItem = new AuctionItem();
                    auctionItem.setSubscription(subscription);
                }

                auctionItem.setBid(bid.getBidFor(subscription));
                auctionItem.setBidder(bid.getBidder());
            }
        }
    }
}
