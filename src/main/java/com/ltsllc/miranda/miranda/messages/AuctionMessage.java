/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.miranda.messages;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.operations.auction.Bid;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Create an auction and include a bid with announcement
 */
public class AuctionMessage extends Message implements Cloneable {
    private Bid bid;

    public Bid getBid() {
        return bid;
    }

    public void setBid(Bid bid) {
        this.bid = bid;
    }

    public AuctionMessage() {}

    public AuctionMessage(BlockingQueue<Message> senderQueue, Object sender, Bid bid) {
        super(Subjects.Auction, senderQueue, sender);
        setBid(bid);
    }

    public Object clone () {
        AuctionMessage auctionMessage = new AuctionMessage();
        deepCopy(this, auctionMessage);
        return auctionMessage;
    }

    public void deepCopy (AuctionMessage original, AuctionMessage clone) {
        super.deepCopy(original, clone);
        clone.setBid((Bid) original.getBid().clone());
    }
}
