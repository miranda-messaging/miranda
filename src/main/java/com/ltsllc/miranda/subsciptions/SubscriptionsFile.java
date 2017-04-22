package com.ltsllc.miranda.subsciptions;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.writer.Writer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/5/2017.
 */
public class SubscriptionsFile extends SingleFile<Subscription> {
    public static final String FILE_NAME = "subscriptions";

    private static SubscriptionsFile ourInstance;

    public static SubscriptionsFile getInstance () {
        return ourInstance;
    }

    public static synchronized void initialize (String filename, Writer writer) {
        if (null == ourInstance) {
            ourInstance = new SubscriptionsFile(writer, filename);
            ourInstance.start();
            ourInstance.load();
        }
    }

    public static void setInstance (SubscriptionsFile subscriptionsFile) {
        ourInstance = subscriptionsFile;
    }

    public SubscriptionsFile (Writer writer, String filename) {
        super(filename, writer);

        SubscriptionsFileReadyState subscriptionsFileReadyState = new SubscriptionsFileReadyState(this);
        setCurrentState(subscriptionsFileReadyState);
    }

    public Type getBasicType () {
        return new TypeToken<ArrayList<Subscription>>() {}.getType();
    }

    public List buildEmptyList () {
        return new ArrayList<Subscription>();
    }

    public Type listType () {
        return new TypeToken<ArrayList<Subscription>>(){}.getType();
    }

    public void checkForDuplicates () {
        List<Subscription> subscriptionsList = new ArrayList<Subscription>(getData());
        List<Subscription> duplicates = new ArrayList<Subscription>();

        for (Subscription current : getData()) {
            for (Subscription subscription : getData()) {
                if (current.getName().equals(subscription.getName()) && current != subscription) {
                    duplicates.add(current);
                }
            }
        }

        getData().removeAll(duplicates);
    }
}
