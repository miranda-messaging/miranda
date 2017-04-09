package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.file.messages.AddSubscriberMessage;
import com.ltsllc.miranda.file.messages.Notification;
import com.ltsllc.miranda.file.messages.RemoveSubscriberMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
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

    private boolean dirty;
    private List<Subscriber> subscribers;

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public SingleFile (String filename, com.ltsllc.miranda.writer.Writer writer) {
        super(filename, writer);

        setDirty(false);
        subscribers = new ArrayList<Subscriber>();
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

    public static String readFile (String filename) {
        FileReader fileReader = null;

        try {
            fileReader = new FileReader(filename);
            StringWriter stringWriter = new StringWriter();
            for (int c = fileReader.read(); c != -1; c = fileReader.read()) {
                stringWriter.append((char) c);
            }

            return stringWriter.toString();
        } catch (IOException e) {
            Panic panic = new Panic("Exception reading file", e, Panic.Reasons.ExceptionReadingFile);
            Miranda.getInstance().panic(panic);
        } finally {
            Utils.closeIgnoreExceptions(fileReader);
        }

        return null;
    }

    public void load ()
    {
        try {
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
                    Utils.closeIgnoreExceptions(fr);
                }

                setData(temp);
            }

            String json = ourGson.toJson(getData());

            Version version = null;
            try {
                version = new Version(json);
            } catch (NoSuchAlgorithmException e) {
                Panic panic = new Panic("Error trying to calculate new version", e, Panic.Reasons.ExceptionTryingToCalculateVersion);
                Miranda.getInstance().panic(panic);
            }
            setVersion(version);

            setLastLoaded(System.currentTimeMillis());
            fireFileLoaded();
        } catch (RuntimeException e) {
            throw new RuntimeException("Exception trying to load " + getFilename(), e);
        }
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

        try {
            updateVersion();
        } catch (NoSuchAlgorithmException x) {
            Panic panic = new Panic ("Exception trying to calculate new version", x, Panic.Reasons.ExceptionTryingToCalculateVersion);
            Miranda.getInstance().panic(panic);
        }

        if (write) {
            getWriter().sendWrite(getQueue(), this, getFilename(), getBytes());
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

    public void sendLoad (BlockingQueue<Message> senderQueue, Object sender) {
        LoadMessage loadMessage = new LoadMessage(senderQueue, sender);
        sendToMe(loadMessage);
    }

    public void merge (List<E> list) {
        List<E> newItems = new ArrayList<E>();

        for (E e : list) {
            if (!contains(e))
                newItems.add(e);
        }

        if (newItems.size() > 0) {
            getData().addAll(newItems);
        }
    }

    public void sendAddSubscriberMessage (BlockingQueue<Message> senderQueue, Object sender, Notification notification) {
        AddSubscriberMessage addSubscriberMessage = new AddSubscriberMessage(senderQueue, sender, notification);
        sendToMe(addSubscriberMessage);
    }

    public void sendRemoveSubscriberMessage (BlockingQueue<Message> senderQueue, Object sender) {
        RemoveSubscriberMessage removeSubscriberMessage = new RemoveSubscriberMessage(senderQueue, sender);
    }

    public void addSubscriber (BlockingQueue<Message> subscriberQueue, Notification notification) {
        Subscriber subscriber = new Subscriber(subscriberQueue, notification);
        getSubscribers().add(subscriber);
    }

    public void removeSubscriber (BlockingQueue<Message> queue) {
        for (Subscriber subscriber : getSubscribers()) {
            if (queue == subscriber.getQueue())
                getSubscribers().remove(subscriber);
        }
    }

    public void fireFileLoaded () {
        for (Subscriber subscriber : getSubscribers()) {
            subscriber.notifySubscriber(getData());
        }
    }

    public void start () {
        super.start();

        load();
    }
}
