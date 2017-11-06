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
import com.ltsllc.miranda.file.states.SingleFileReadyState;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 5/18/2017.
 */
public class DeliveriesFileReadyState extends SingleFileReadyState {
    public static final String NAME = "deliveries file";

    public DeliveriesFile getDeliveriesFile () {
        return (DeliveriesFile) getContainer();
    }

    public String getName () {
        return NAME;
    }

    public DeliveriesFileReadyState (DeliveriesFile deliveriesFile) throws MirandaException {
        super(deliveriesFile);
    }

    public Type getListType () {
        return new TypeToken<List<Delivery>> () {}.getType();
    }

    public List getPerishables() {
        return new ArrayList(getDeliveriesFile().getData());
    }
}
