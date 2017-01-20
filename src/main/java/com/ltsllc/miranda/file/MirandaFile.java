package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import com.ltsllc.miranda.util.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
abstract public class MirandaFile {
    abstract public byte[] getBytes();
    private Logger logger = Logger.getLogger(MirandaFile.class);

    private String filename;
    private BlockingQueue<Message> writerQueue;

    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public void setWriterQueue (BlockingQueue<Message> queue) {
        writerQueue = queue;
    }



    public MirandaFile (String filename, BlockingQueue<Message> queue)
    {
        this.filename = filename;
        this.writerQueue = queue;
    }


    public void fileChanged (String s) {
        load();
    }

    public MirandaFile (String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }


    public void write (String filename, byte[] array)
    {
        Message m = new WriteMessage(filename, array);
        try {
            getWriterQueue().put(m);
        } catch (InterruptedException e) {
            logger.fatal("Exception while trying to send message to writer", e);
            System.exit(1);
        }
    }


    abstract public void load ();

    public void write () {
        write(getFilename(), getBytes());
    }

    public void watch () {
        try {
            Method method = this.getClass().getMethod("fileChanged", Void.TYPE);
            Miranda.getFileWatcher().watchForChanges(getFilename(), this, method);
        } catch (NoSuchMethodException e) {
            logger.fatal("Exception trying to register file watcher", e);
            System.exit(1);
        }
    }
}
