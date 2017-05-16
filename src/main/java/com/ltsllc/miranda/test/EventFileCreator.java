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

package com.ltsllc.miranda.test;

/**
 * Created by Clark on 2/24/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.util.ImprovedRandom;
import com.ltsllc.miranda.util.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a file with random {@link Event} objects in it.
 */
public class EventFileCreator implements FileCreator {
    private static Gson ourGson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private ImprovedRandom random;
    private int maxNumberOfEvents;
    private int maxContentSize;

    public ImprovedRandom getRandom() {
        return random;
    }

    public EventFileCreator (ImprovedRandom random, int maxNumberOfEvents, int maxContentSize) {
        this.random = random;
        this.maxNumberOfEvents = maxNumberOfEvents;
        this.maxContentSize = maxContentSize;
    }

    public boolean createFile(File file) {
        int size = random.nextIndex(maxNumberOfEvents);
        List<Event> list = new ArrayList<Event>();

        for (int count = 0; count <= size; count++) {
            Event event = Event.createRandom (random, maxContentSize);
            list.add(event);
        }

        String json = ourGson.toJson(list);

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(json);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            Utils.closeIgnoreExceptions(fileWriter);
        }

        return true;
    }
}
