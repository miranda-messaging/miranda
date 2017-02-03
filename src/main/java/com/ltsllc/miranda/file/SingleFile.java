package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import com.ltsllc.miranda.User;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
abstract public class SingleFile<E> extends MirandaFile {
    private static Logger logger = Logger.getLogger(SingleFile.class);

    public SingleFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }

    private Gson ourGson;

    public Gson getGson() {
        if (null == ourGson)
            ourGson = new Gson();

        return ourGson;
    }

    protected List<E> data;

    public List<E> getData () {
        return data;
    }

    public void setData (List<E> list) {
        this.data = list;
    }


    private boolean execptionOnLoadIsFatal = false;


    public boolean exceptionOnLoadIsFatal() {
        return execptionOnLoadIsFatal;
    }

    public static <T> List<T> mapFromJsonArray(String respInArray, Type listType) {
        List<T> ret = new Gson().fromJson(respInArray, listType);
        return ret;
    }

    public void load ()
    {
        logger.info("loading " + getFilename());
        File f = new File(getFilename());
        if (!f.exists()) {
            setData(null);
        } else {




            Gson gson = new Gson();
            FileReader fr = null;
            List<E> temp = null;



            try {
                fr = new FileReader(getFilename());
                Type t = new TypeToken<ArrayList<E>>(){}.getType();
                temp = gson.fromJson(fr, t);
            } catch (FileNotFoundException e) {
                logger.info(getFilename() + " not found");
            } finally {
                IOUtils.closeNoExceptions(fr);
            }


            setData(temp);
        }
    }


    public byte[] getBytes () {
        String json = getGson().toJson(getData());
        return json.getBytes();
    }


}
