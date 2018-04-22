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

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Delivery;
import com.ltsllc.miranda.file.states.MirandaFileReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.test.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/22/2017.
 */
public class TestDeliveriesFile extends TestCase {
    private DeliveriesFile deliveriesFile;

    public DeliveriesFile getDeliveriesFile() {
        return deliveriesFile;
    }

    @Before
    public void setup () throws Exception {
        reset();

        super.setup();

        setupMiranda();
        setupMockReader();
        setupMockWriter();
        setupMirandaProperties();
        MirandaProperties properties = Miranda.properties;

        String directory = properties.getProperty(MirandaProperties.PROPERTY_DELIVERY_DIRECTORY);

        try {
            this.deliveriesFile = new DeliveriesFile(directory, getMockReader(), getMockWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testConstructor () {
        assert (getDeliveriesFile().getCurrentState() instanceof MirandaFileReadyState);
    }

    @Test
    public void testListType () {
        Type theirType = getDeliveriesFile().getListType();
        Type localType = new TypeToken<List<Delivery>>() {}.getType();

        assert (theirType.equals(localType));
    }

    @Test
    public void testBuildEmptyList () throws MirandaException {
        setupMiranda();
        setupMockReader();
        List<Delivery> local = new ArrayList<Delivery>();
        List theirs = getDeliveriesFile().buildEmptyList();

        assert (local.equals(theirs));
    }

    @Test
    public void testEquals () {
        assert (getDeliveriesFile().equals(getDeliveriesFile()));
    }
}
