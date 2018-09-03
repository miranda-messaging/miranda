package com.ltsllc.miranda.operations.auction;

import java.util.HashMap;
import java.util.Map;

/**
 * An bid from a node
 */
public class Bid implements Cloneable {
    private String bidder;
    private Map<String, Long> bids = new HashMap<>();

    public String getBidder() {
        return bidder;
    }

    public void setBidder(String bidder) {
        this.bidder = bidder;
    }

    public Map<String, Long> getBids() {
        return bids;
    }

    public void setBids(Map<String, Long> bids) {
        this.bids = bids;
    }

    public Long getBidFor(String topic) {
        return bids.get(topic);
    }

    public Bid (String bidder, Map<String, Long> map) {
        setBidder(bidder);
        setBids(map);
    }

    public Bid () {}

    public Object clone () {
        Bid clone = new Bid();
        deepCopy(this, clone);
        return clone;
    }

    public void deepCopy (Bid original, Bid clone) {
        clone.setBidder(original.getBidder());
        Map<String, Long> newMap = new HashMap<>();
        newMap.putAll(bids);
        clone.setBids(newMap);
    }
}
