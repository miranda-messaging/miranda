package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Delivery;
import com.ltsllc.miranda.Message;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class DeliveriesFile extends SingleFile<Delivery> {

    public DeliveriesFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }

    public Type listType() {
        return new TypeToken<List<Delivery>>() {}.getType();
    }

    public List buildEmptyList() {
        return new ArrayList<Delivery>();
    }

}
