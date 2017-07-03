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
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
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

    public static synchronized void initialize (String filename, Reader reader, Writer writer) throws IOException {
        if (null == ourInstance) {
            ourInstance = new SubscriptionsFile(reader, writer, filename);
            ourInstance.start();
            ourInstance.load();
        }
    }

    public static void setInstance (SubscriptionsFile subscriptionsFile) {
        ourInstance = subscriptionsFile;
    }

    public SubscriptionsFile (Reader reader, Writer writer, String filename) throws IOException {
        super(filename, reader, writer);

        SubscriptionsFileStartingState subscriptionsFileStartingState = new SubscriptionsFileStartingState(this);
        setCurrentState(subscriptionsFileStartingState);
    }

    public Type getBasicType () {
        return new TypeToken<ArrayList<Subscription>>() {}.getType();
    }

    public List buildEmptyList () {
        return new ArrayList<Subscription>();
    }

    public Type getListType() {
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
