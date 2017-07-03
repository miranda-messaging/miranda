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

package com.ltsllc.miranda.subsciptions;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.clientinterface.basicclasses.User;
import com.ltsllc.miranda.file.states.SingleFileReadyState;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
public class SubscriptionsFileReadyState extends SingleFileReadyState {
    public static String NAME = "subscriptions";

    private static Logger logger = Logger.getLogger(SubscriptionsFileReadyState.class);

    private SubscriptionsFile subscriptionsFile;

    public SubscriptionsFileReadyState (SubscriptionsFile subscriptionsFile) {
        super(subscriptionsFile);

        this.subscriptionsFile = subscriptionsFile;
    }

    public SubscriptionsFile getSubscriptionsFile() {
        return subscriptionsFile;
    }

    @Override
    public void write() {
        WriteMessage writeMessage = new WriteMessage(getSubscriptionsFile().getFilename(), getSubscriptionsFile().getBytes(),
                getSubscriptionsFile().getQueue(), this);

        send(getSubscriptionsFile().getQueue(), writeMessage);
    }


    @Override
    public void add(Object o) {
        Subscription subscription = (Subscription) o;
        getSubscriptionsFile().getData().add(subscription);
    }


    @Override
    public boolean contains(Object o) {
        Subscription subscription = (Subscription) o;
        for (Subscription sub : getSubscriptionsFile().getData()) {
            if (sub.equals(subscription))
                return true;
        }

        return false;
    }


    @Override
    public Type getListType() {
        return new TypeToken<List<User>>() {}.getType();
    }

    @Override
    public String getName() {
        return NAME;
    }
}
