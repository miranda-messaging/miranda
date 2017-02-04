package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
abstract public class MirandaFile extends Consumer{
    abstract public void load ();
    abstract public byte[] getBytes();

    private Logger logger = Logger.getLogger(MirandaFile.class);

    private String filename;
    private BlockingQueue<Message> writerQueue;


    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public MirandaFile (String filename, BlockingQueue<Message> queue)
    {
        super("flie");

        this.filename = filename;
        this.writerQueue = queue;
    }


    public MirandaFile (String filename) {
        super("file");
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void fileChanged (String s) {
        load();
    }

    public void write (String filename, byte[] array)
    {
        Message m = new WriteMessage(filename, array, null);
        try {
            getWriterQueue().put(m);
        } catch (InterruptedException e) {
            logger.fatal("Exception while trying to send message to writer", e);
            System.exit(1);
        }
    }


    public void fileChanged () {
        logger.info(getFilename() + " changed");
    }

    public void write () {
        write(getFilename(), getBytes());
    }

    public void watch () {
        try {
            Method method = this.getClass().getMethod("fileChanged");
            Miranda.getFileWatcher().watchForChanges(getFilename(), this, method);
        } catch (NoSuchMethodException e) {
            logger.fatal("Exception trying to register file watcher", e);
            System.exit(1);
        }
    }
}
