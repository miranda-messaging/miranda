package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
abstract public class MirandaFile extends Consumer implements Comparer {
    abstract public void load ();
    abstract public byte[] getBytes();


    private Logger logger = Logger.getLogger(MirandaFile.class);

    private String filename;
    private BlockingQueue<Message> writerQueue;
    private List<Perishable> elements = new ArrayList<Perishable>();
    private Version version;
    private long lastLoaded = -1;
    private long lastCollection;

    public long getLastCollection() {
        return lastCollection;
    }

    public void setLastCollection(long lastCollection) {
        this.lastCollection = lastCollection;
    }

    public BlockingQueue<Message> getWriterQueue() {
        return writerQueue;
    }

    public List<Perishable> getElements() {
        return elements;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion (Version version) {
        this.version = version;
    }

    public long getLastLoaded() {
        return lastLoaded;
    }

    public void setLastLoaded(long lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    public MirandaFile (String filename, BlockingQueue<Message> queue)
    {
        super("file");

        this.filename = filename;
        this.writerQueue = queue;
        this.lastCollection = -1;
    }


    public MirandaFile (String filename) {
        super("file");
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void write (String filename, byte[] array)
    {
        Message m = new WriteMessage(filename, array, null, null);
        try {
            getWriterQueue().put(m);
        } catch (InterruptedException e) {
            logger.fatal("Exception while trying to send message to writer", e);
            System.exit(1);
        }
    }

    public void write () {
        write(getFilename(), getBytes());
    }

    public void fileChanged () {
        logger.info(getFilename() + " changed");
        load();
    }

    public void watch () {
        try {
            Method method = this.getClass().getMethod("fileChanged");
            File file = new File(getFilename());
            FileChangedMessage fileChangedMessage = new FileChangedMessage(getQueue(), this, file);
            WatchMessage message = new WatchMessage(getQueue(), this, file, fileChangedMessage);
            send(message, Miranda.fileWatcher.getQueue());
        } catch (NoSuchMethodException e) {
            logger.fatal("Exception trying to register file watcher", e);
            System.exit(1);
        }
    }

    public void updateVersion () {
        StringWriter stringWriter = new StringWriter();

        for (Perishable perishable : getElements()) {
            stringWriter.write(perishable.toJson());
        }

        Version version = new Version(stringWriter.toString());
        setVersion(version);
    }


    public boolean equals (Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof MirandaFile))
            return false;

        MirandaFile other = (MirandaFile) o;

        if (
            !getFilename().equals(other.getFilename())
            || !getWriterQueue().equals(other.getWriterQueue())
            || !getElements().equals(other.getElements())
            || !getVersion().equals(other.getVersion())
        )
        {
            return false;
        }
        else {
            return super.equals(o);
        }
    }


    public boolean compare (Map<Object,Boolean> map, Object o)
    {
        if (map.containsKey(o))
            return map.get(o).booleanValue();

        if (o == this)
            return true;

        if (null == o || !(o instanceof MirandaFile))
        {
            map.put(o, Boolean.FALSE);
            return false;
        }

        map.put (o, Boolean.TRUE);
        MirandaFile other = (MirandaFile) o;

        if (
            !getFilename().equals(other.getFilename())
            || !getWriterQueue().equals(other.getWriterQueue())
            || !getElements().equals(other.getElements())
            || !getVersion().equals(other.getVersion())
        )
        {
            map.put(o, Boolean.FALSE);
            return false;
        }

        return super.compare(map, o);
    }

    public String toString () {
        return filename;
    }
}
