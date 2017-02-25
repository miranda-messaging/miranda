package com.ltsllc.miranda.util.file;

/**
 * Created by Clark on 2/24/2017.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.Utils;
import com.ltsllc.miranda.event.Event;
import com.ltsllc.miranda.util.ImprovedRandom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
