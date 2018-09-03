package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.operations.auction.Auction;

import java.util.concurrent.BlockingQueue;

/**
 * who owns a subscription: auction results
 */
public class AuctionResultsMessage extends Message {
    private Auction auction;

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public AuctionResultsMessage (BlockingQueue<Message> queue, Object senderObject, Auction auction) {
        super(Subjects.AuctionResults, queue, senderObject);
        setAuction(auction);
    }
}
