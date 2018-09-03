package com.ltsllc.miranda.operations.auction;

import java.util.Map;

/**
 * A redistribution of topic
 */
public class Auction {
    private Map<String, AuctionItem>  topicToAuctionItem;

    public Map<String, AuctionItem> getTopicToAuctionItem() {
        return topicToAuctionItem;
    }

    public void setTopicToAuctionItem(Map<String, AuctionItem> topicToAuctionItem) {
        this.topicToAuctionItem = topicToAuctionItem;
    }

    public void recordBid (Bid bid) {
        for (String topic : bid.getBids().keySet()) {
            if (topicToAuctionItem.get(topic).getBid() < bid.getBidFor(topic)) {
                AuctionItem auctionItem = topicToAuctionItem.get(topic);
                if (null == auctionItem) {
                    auctionItem = new AuctionItem();
                    auctionItem.setTopic(topic);
                }

                auctionItem.setBid(bid.getBidFor(topic));
                auctionItem.setBidder(bid.getBidder());
            }
        }
    }
}
