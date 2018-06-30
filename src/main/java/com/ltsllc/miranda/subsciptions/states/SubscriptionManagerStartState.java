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

package com.ltsllc.miranda.subsciptions.states;

import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.manager.states.ManagerStartState;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.subsciptions.SubscriptionManager;

/**
 * Created by Clark on 5/14/2017.
 */
public class SubscriptionManagerStartState extends ManagerStartState {
    public SubscriptionManager getSubscriptionManager() {
        return (SubscriptionManager) getContainer();
    }

    public SubscriptionManagerStartState(SubscriptionManager subscriptionManager) throws MirandaException {
        super(subscriptionManager, new SubscriptionManagerReadyState(subscriptionManager));
    }

    public State getReadyState() {
        try {
            return new SubscriptionManagerReadyState(getSubscriptionManager());
        } catch (MirandaException e) {
            Panic panic = new Panic("exception in getReadyState", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return null;
        }
    }
}
