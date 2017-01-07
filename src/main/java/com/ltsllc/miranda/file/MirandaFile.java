package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.util.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
public class MirandaFile<E> {
    private Logger logger = Logger.getLogger(MirandaFile.class);

    private BlockingQueue<Message> writerQueue;

    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public void setWriterQueue (BlockingQueue<Message> queue) {
        writerQueue = queue;
    }

    private String filename;

    public String getFilename ()
    {
        return filename;
    }

    public void setFilename (String s) {
        filename = s;
    }

    private E[] data;

    public E[] getData () {
        return data;
    }

    public void setData (E[] data) {
        this.data = data;
    }


    public MirandaFile (BlockingQueue<Message> queue, String s)
    {
        setWriterQueue(queue);
        setFilename(s);
    }


    public void send (Message m)
    {
        try {
            getWriterQueue().put(m);
        } catch (InterruptedException e) {
            logger.error("Exception while trying to send message", e);
        }
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
        } catch (FileNotFoundException e) {
            logger.error("Error trying to read " + getFilename(), e);
        } finally {
            if (fr != null) {
                IOUtils.closeNoExceptions(fr);
            }
        }

        setData(data);
    }

    public void write () {
        String s;
        Gson gson = new Gson();
        s = gson.toJson(getData());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = baos.toByteArray();

        send(new WriteMessage(getFilename(), b));
    }
}
