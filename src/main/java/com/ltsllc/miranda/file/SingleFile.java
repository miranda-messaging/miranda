package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import com.ltsllc.miranda.util.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
public class SingleFile<E> extends MirandaFile {
    private Logger logger = Logger.getLogger(SingleFile.class);

    public SingleFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }

    private Gson ourGson;

    public Gson getGson() {
        if (null == ourGson)
            ourGson = new Gson();

        return ourGson;
    }

    private E[] data;

    public E[] getData () {
        return data;
    }

    public void setData (E[] data) {
        this.data = data;
    }


    private boolean execptionOnLoadIsFatal;


    public boolean exceptionOnLoadIsFatal() {
        return execptionOnLoadIsFatal;
    }


    public void load ()
    {
        File f = new File(getFilename());
        if (!f.exists())
            setData(null);

        Gson gson = new Gson();
        FileReader fr = null;
        E[]data = null;

        try {
            fr = new FileReader(f);
            Type listType = new TypeToken<ArrayList<E>>(){}.getType();
            data = gson.fromJson(fr, listType);
        } catch (IOException e) {
            if (exceptionOnLoadIsFatal()) {
                logger.fatal("Exception when trying to load " + getFilename(), e);
                System.exit(1);
            } else {
                logger.error("Exception when trying to load " + getFilename(), e);
            }
        } finally {
            IOUtils.closeNoExceptions(fr);
        }

        setData(data);
    }




    public byte[] getBytes () {
        Type listType = new TypeToken<ArrayList<E>>(){}.getType();
        String json = getGson().toJson(getData());
        return json.getBytes();

    }

}
