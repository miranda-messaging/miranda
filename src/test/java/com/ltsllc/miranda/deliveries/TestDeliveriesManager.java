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

package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Clark on 5/13/2017.
 */
public class TestDeliveriesManager extends TestCase {
    private DeliveryManager deliveryManager;

    public DeliveryManager getDeliveryManager() {
        return deliveryManager;
    }

    public void reset () throws MirandaException {
        super.reset();

        this.deliveryManager = null;
    }

    @Before
    public void setup () {
        try {
            reset();

            super.setup();

            setupMockMiranda();
            setupMockFileWatcher();

            this.deliveryManager = new DeliveryManager("testdir", 1000000, getMockReader(), getMockWriter());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor () {
        assert (getDeliveryManager().getName().equals(DeliveryManager.NAME));
        assert (getDeliveryManager().getReader() == getMockReader());
        assert (getDeliveryManager().getWriter() == getMockWriter());
    }
}
