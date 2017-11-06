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

package com.ltsllc.miranda.servlet.subscription;

import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Subscription;
import com.ltsllc.miranda.clientinterface.results.Results;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.servlet.ServletHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Clark on 4/22/2017.
 */
public class SubscriptionHolder extends ServletHolder {
    private static SubscriptionHolder ourInstance;

    private List<Subscription> subscriptionsList;
    private Subscription subscription;
    private Results getResult;
    private Results createResult;
    private Results updateResult;
    private Results deleteResult;

    public List<Subscription> getSubscriptionList () {
        if (subscriptionsList == null)
            subscriptionsList = new ArrayList<Subscription>();

        return subscriptionsList;
    }

    public void setSubscriptionsList (List<Subscription> list) {
        this.subscriptionsList = list;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Results getGetResult() {
        return getResult;
    }

    public void setGetResult(Results getResult) {
        this.getResult = getResult;
    }

    public static SubscriptionHolder getInstance () {
        return ourInstance;
    }

    public static void setInstance (SubscriptionHolder subscriptionHolder) {
        ourInstance = subscriptionHolder;
    }

    public static void initialize (long timeout) throws MirandaException {
        ourInstance = new SubscriptionHolder(timeout);
    }

    public Results getDeleteResult() {
        return deleteResult;
    }

    public void setDeleteResult(Results deleteResult) {
        this.deleteResult = deleteResult;
    }

    public Results getUpdateResult() {
        return updateResult;
    }

    public void setUpdateResult(Results updateResult) {
        this.updateResult = updateResult;
    }

    public Results getCreateResult() {
        return createResult;
    }

    public void setCreateResult(Results createResult) {
        this.createResult = createResult;
    }

    public SubscriptionHolder (long timeout) throws MirandaException {
        super ("subscription holder", timeout);

        SubscriptionHolderReadyState readyState = new SubscriptionHolderReadyState(this);
        setCurrentState(readyState);
    }

    public List<Subscription> getSubscriptions () throws TimeoutException {
        setSubscriptionsList(null);

        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionsMessage(getQueue(), this);

        sleep();

        return getSubscriptionList();
    }

    public Subscription getSubscription (String name) throws TimeoutException {
        setGetResult(Results.Unknown);

        Miranda.getInstance().getSubscriptionManager().sendGetSubscriptionMessage(getQueue(), this, name);

        sleep();

        return getSubscription();
    }

    public Results createSubscription (Subscription subscription) throws TimeoutException {
        setCreateResult(Results.Unknown);

        Miranda.getInstance().sendCreateSubscriptionMessage(getQueue(), this, getSession(), subscription);

        sleep();

        return getCreateResult();
    }

    public Results updateSubscription (Subscription subscription) throws TimeoutException {
        setUpdateResult(Results.Unknown);

        Miranda.getInstance().sendUpdateSubscriptionMessage(getQueue(), this, getSession(), subscription);

        sleep();

        return getUpdateResult();
    }

    public Results deleteSubscription (String name) throws TimeoutException {
        setDeleteResult(Results.Unknown);

        Miranda.getInstance().sendDeleteSubscriptionMessage (getQueue(), this, getSession(), name);

        sleep();

        return getDeleteResult();
    }

    public void setSubscriptionsAndAwaken (List<Subscription> subscriptions) {
        setSubscriptionsList(subscriptions);

        wake();
    }

    public void setSubscriptionAndAwaken (Results result, Subscription subscription) {
        setGetResult(result);
        setSubscription(subscription);

        wake();
    }

    public void setCreateResultAndAwaken (Results result) {
        setCreateResult(result);

        wake();
    }

    public void setUpdateResultAndAwaken (Results result) {
        setUpdateResult(result);

        wake();
    }

    public void setDeleteResultAndAwaken (Results result) {
        setDeleteResult(result);

        wake();
    }
}
