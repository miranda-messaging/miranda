package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Clark on 5/13/2017.
 */
public class TestDeliveriesManager extends TestCase {
    private DeliveryManager deliveryManager;

    public DeliveryManager getDeliveryManager() {
        return deliveryManager;
    }

    public void reset () {
        this.deliveryManager = null;
    }

    @Before
    public void setup () {
        this.deliveryManager = new DeliveryManager("testdir", getMockReader(), getMockWriter());
    }

    @Test
    public void testConstructor () {
        assert (getDeliveryManager().getName().equals(DeliveryManager.NAME));
        assert (getDeliveryManager().getReader() == getMockReader());
        assert (getDeliveryManager().getWriter() == getMockWriter());
    }
}
