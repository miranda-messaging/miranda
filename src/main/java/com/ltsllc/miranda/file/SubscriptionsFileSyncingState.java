package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Subscription;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
public class SubscriptionsFileSyncingState extends SingleFileSyncingState {
    private SubscriptionsFile subscriptionsFile;

    public SubscriptionsFileSyncingState (SubscriptionsFile subscriptionsFile) {
        super(subscriptionsFile);

        this.subscriptionsFile = subscriptionsFile;
    }

    public SubscriptionsFile getSubscriptionsFile() {
        return subscriptionsFile;
    }

    @Override
    public Type getListType() {
        return new TypeToken<List<Subscription>>() {}.getType();
    }


    @Override
    public boolean contains(Object o) {
        Subscription otherSubscription = (Subscription) o;

        for (Subscription subscription : getSubscriptionsFile().getData()) {
            if (subscription.equals(otherSubscription))
                return true;
        }

        return false;
    }


    @Override
    public State getReadyState() {
        return new SubscriptionsFileSyncingState(getSubscriptionsFile());
    }


    @Override
    public List getData() {
        return getSubscriptionsFile().getData();
    }


    @Override
    public String getName() {
        return "subscriptions";
    }


    @Override
    public SingleFile getFile() {
        return getSubscriptionsFile();
    }
}
