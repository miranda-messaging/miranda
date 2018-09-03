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

package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.AuctionMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.operations.OperationRegistry;
import com.ltsllc.miranda.property.MirandaProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/22/2017.
 *
 * Where each node 'bids' on each subscription
 */
public class AuctionOperation extends Consumer {
    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    private Auction auction;
    private int numberWaiting;

    public int getNumberWaiting() {
        return numberWaiting;
    }

    public void setNumberWaiting(int numberWaiting) {
        this.numberWaiting = numberWaiting;
    }

    public int decrementNumberWaiting () {
        numberWaiting--;
        return numberWaiting;
    }

    public AuctionOperation() throws MirandaException {
        super("auction operations");
        AuctionOperationStartState auctionStartState = new AuctionOperationStartState(this);
        setCurrentState(auctionStartState);
        OperationRegistry.getInstance().register("auction", this);
    }

    public void recordBid(Bid bid) {
        getAuction().recordBid(bid);
    }

    public void sendAuction(BlockingQueue<Message> queue, Object senderObject, Bid bid) {
        AuctionMessage auctionMessage = new AuctionMessage(queue, senderObject, bid);
        sendToMe(auctionMessage);
    }

    public void done () {
        OperationRegistry.getInstance().unregister("auction");
    }
}
