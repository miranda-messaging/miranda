package com.ltsllc.miranda.operations.auction;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;

/**
 * Created by Clark on 5/15/2017.
 */
public class TestAuctionOperation extends TestCase{
    private AuctionOperation auctionOperation;

    public AuctionOperation getAuctionOperation() {
        return auctionOperation;
    }

    public void reset () {
        super.reset();

        auctionOperation = null;
    }

    @Before
    public void setup () {
        reset();

        super.setup();

        auctionOperation = new AuctionOperation();
    }
}
