package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Utils;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.util.IOUtils;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
abstract public class SingleFile<E extends Perishable> extends MirandaFile {
    abstract public List buildEmptyList();
    abstract public Type listType();

    private static Logger logger = Logger.getLogger(SingleFile.class);

    private Version version;

    public SingleFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);
    }

    private Gson ourGson = buildGson();

    public Gson getGson() {
        if (null == ourGson)
            ourGson = new Gson();

        return ourGson;
    }

    protected List<E> data = buildEmptyList();

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

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
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

            String sha1 = calculateSha1();
            Version version = new Version(sha1, f.lastModified());
            setVersion(version);
        }
    }


    public byte[] getBytes () {
        String json = getGson().toJson(getData());
        return json.getBytes();
    }


    private static Gson buildGson () {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.setPrettyPrinting().create();
    }


    private static final int BUFFER_SIZE = 8192;

    public String calculateSha1() {
        FileInputStream fileInputStream = null;
        byte[] digest = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA");
            fileInputStream = new FileInputStream(getFilename());
            byte[] buffer = new byte[BUFFER_SIZE];

            int bytesRead;

            do {
                bytesRead = fileInputStream.read(buffer);
                messageDigest.update(buffer, 0, bytesRead);
            } while (bytesRead >= BUFFER_SIZE);

            digest = messageDigest.digest();
        } catch (Exception e) {
            logger.fatal("Exception while trying to calculate sha1", e);
            System.exit(1);
        }finally {
            Utils.closeIgnoreExceptions(fileInputStream);
        }

        return Utils.bytesToString(digest);
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

        if (write) {
            WriteMessage writeMessage = new WriteMessage(getFilename(), getBytes(), getQueue(), this);
            send(writeMessage, getWriterQueue());
        }
    }
}
