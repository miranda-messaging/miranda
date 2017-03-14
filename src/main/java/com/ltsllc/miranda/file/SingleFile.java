package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.util.IOUtils;
import com.ltsllc.miranda.writer.*;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
abstract public class SingleFile<E> extends MirandaFile implements Comparer {
    abstract public List buildEmptyList();
    abstract public Type listType();

    private static Logger logger = Logger.getLogger(SingleFile.class);

    private static Gson ourGson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public SingleFile (String filename, com.ltsllc.miranda.writer.Writer writer) {
        super(filename, writer);
    }

    private List<E> data = buildEmptyList();

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
            List list = buildEmptyList();
            setData(list);
        } else {
            Gson gson = new Gson();
            FileReader fr = null;
            List<E> temp = null;
            try {
                fr = new FileReader(getFilename());
                temp = ourGson.fromJson(fr, listType());
            } catch (FileNotFoundException e) {
                logger.info(getFilename() + " not found");
            } finally {
                IOUtils.closeNoExceptions(fr);
            }

            setData(temp);
        }

        String json = ourGson.toJson(getData());
        Version version = new Version(json);
        setVersion(version);

        setLastLoaded(System.currentTimeMillis());
    }


    public byte[] getBytes () {
        String json = ourGson.toJson(getData());
        return json.getBytes();
    }


    public boolean contains (E e) {
        for (E contained : getData()) {
            if (contained.equals(e))
                return true;
        }

        return false;
    }


    public void add (E e)
    {
        add(e, true);
    }

    public void add(E e, boolean write) {
        getData().add(e);
        updateVersion();

        if (write) {
            WriteMessage writeMessage = new WriteMessage(getFilename(), getBytes(), getQueue(), this);
            send(writeMessage, getWriterQueue());
        }
    }


    public boolean equals (Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof SingleFile))
            return false;

        SingleFile other = (SingleFile) o;
        return getVersion().equals(other.getVersion());
    }


    public boolean compare (Map<Object,Boolean> map, Object o) {
        if (map.containsKey(o))
            return map.get(o).booleanValue();

        if (this == o)
            return true;

        if (null == o || !(o instanceof SingleFile)) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        SingleFile other = (SingleFile) o;

        if (!getData().equals(other.getData())) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        return super.compare(map, o);
    }
}
