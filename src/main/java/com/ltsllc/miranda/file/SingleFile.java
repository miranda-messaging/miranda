/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.file;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ltsllc.common.util.Utils;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.clientinterface.basicclasses.MergeException;
import com.ltsllc.miranda.clientinterface.basicclasses.MirandaObject;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.file.messages.AddObjectsMessage;
import com.ltsllc.miranda.file.messages.RemoveObjectsMessage;
import com.ltsllc.miranda.file.messages.UpdateObjectsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.Reader;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
abstract public class SingleFile<E extends MirandaObject> extends MirandaFile implements Comparer {
    abstract public List buildEmptyList();
    abstract public Type getListType();
    abstract public void checkForDuplicates();

    private static Logger logger = Logger.getLogger(SingleFile.class);

    private static Gson ourGson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    protected SingleFile () {}

    public SingleFile(String filename, Reader reader, com.ltsllc.miranda.writer.Writer writer) throws IOException {
        super(filename, reader, writer);

        setDirty(false);
    }

    private List<E> data = buildEmptyList();

    public List<E> getData() {
        return data;
    }

    public void setData(List<E> list) {
        this.data = list;
    }

    public void setData (byte[] data) {
        if (null == data) {
            this.data = new ArrayList();
        } else {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
            InputStreamReader inputStreamReader = null;

            try {
                inputStreamReader = new InputStreamReader(byteArrayInputStream);
                this.data = getGson().fromJson(inputStreamReader, getListType());
            } catch (Exception e) {
                Panic panic = new Panic("Exception loading list", e, Panic.Reasons.ExceptionLoadingFile);
                Miranda.panicMiranda(panic);
            } finally {
                Utils.closeIgnoreExceptions(inputStreamReader);
            }
        }
    }

    private boolean execptionOnLoadIsFatal = false;

    public boolean exceptionOnLoadIsFatal() {
        return execptionOnLoadIsFatal;
    }

    public static <T> List<T> mapFromJsonArray(String respInArray, Type listType) {
        List<T> ret = new Gson().fromJson(respInArray, listType);
        return ret;
    }

    public void readFile(String filename) {
        getReader().sendReadMessage(getQueue(), this, filename);
    }

    public void load() {
        Miranda.getInstance().getReader().sendReadMessage(getQueue(), this, getFilename());
    }

    public void processData(byte[] data) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
        List<E> temp = null;
        temp = getGson().fromJson(inputStreamReader, getListType());
        setData(temp);
        updateVersion();
        setLastLoaded(System.currentTimeMillis());
        fireFileLoaded();
    }

    public void updateVersion() {
        try {
            String json = getGson().toJson(getData());
            Version version = new Version(json);
            setVersion(version);
        } catch (GeneralSecurityException e) {
            Panic panic = new Panic("Exception calculating new version", e, Panic.Reasons.ExceptionTryingToCalculateVersion);
            Miranda.getInstance().panic(panic);
        }
    }

    public byte[] getBytes() {
        String json = ourGson.toJson(getData());
        return json.getBytes();
    }


    public boolean contains(Object o) {
        E e = (E) o;
        for (E contained : getData()) {
            if (contained.isEquivalentTo(e))
                return true;
        }

        return false;
    }


    public void add(E e) {
        add(e, true);
    }

    public void add(E e, boolean write) {
        getData().add(e);
        updateVersion();

        if (write) {
            getWriter().sendWrite(getQueue(), this, getFilename(), getBytes());
        }
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof SingleFile))
            return false;

        SingleFile other = (SingleFile) o;
        return getVersion().equals(other.getVersion());
    }


    public boolean compare(Map<Object, Boolean> map, Object o) {
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

    public void sendLoad(BlockingQueue<Message> senderQueue, Object sender) {
        LoadMessage loadMessage = new LoadMessage(senderQueue, sender);
        sendToMe(loadMessage);
    }

    public void merge(List<E> list) {
        List<E> newItems = new ArrayList<E>();

        for (E e : list) {
            if (!contains(e))
                newItems.add(e);
        }

        if (newItems.size() > 0) {
            getData().addAll(newItems);
        }
    }

    public void addSubscriber(BlockingQueue<Message> subscriberQueue) {
        Subscriber subscriber = new Subscriber(subscriberQueue);
        getSubscribers().add(subscriber);
    }

    public void removeSubscriber(BlockingQueue<Message> queue) {
        for (Subscriber subscriber : getSubscribers()) {
            if (queue == subscriber.getQueue())
                getSubscribers().remove(subscriber);
        }
    }

    public void start() {
        super.start();

        load();
    }

    public void sendRemoveObjectsMessage(BlockingQueue<Message> senderQueue, Object sender, List<E> objects) {
        RemoveObjectsMessage removeObjectsMessage = new RemoveObjectsMessage(senderQueue, sender, objects);
        sendToMe(removeObjectsMessage);
    }

    public void sendAddObjectsMessage(BlockingQueue<Message> senderQueue, Object sender, E object) {
        List<E> objects = new ArrayList<E>();
        objects.add(object);
        sendAddObjectsMessage(senderQueue, sender, objects);
    }

    public void sendAddObjectsMessage(BlockingQueue<Message> senderQueue, Object sender, List<E> objects) {
        AddObjectsMessage addObjectsMessage = new AddObjectsMessage(senderQueue, sender, objects);
        sendToMe(addObjectsMessage);
    }

    public void sendUpdateObjectsMessage(BlockingQueue<Message> senderQueue, Object sender, E updatedObject) {
        List<E> updatedObjects = new ArrayList<E>();
        updatedObjects.add(updatedObject);

        UpdateObjectsMessage updateObjectsMessage = new UpdateObjectsMessage(senderQueue, sender, updatedObjects);
        sendToMe(updateObjectsMessage);
    }

    public void sendRemoveObjectsMessage(BlockingQueue<Message> senderQueue, Object sender, E object) {
        List<E> objects = new ArrayList<E>();
        objects.add(object);
        sendRemoveObjectsMessage(senderQueue, sender, objects);
    }

    public void addObjects(List list) {
        List<E> newObjects = (List<E>) list;
        for (E object : newObjects) {
            if (!contains(object))
                getData().add(object);
        }

        write();
    }

    public void updateObjects(List<E> updatedObjects) throws MergeException {
        for (E updatedObject : updatedObjects) {
            update(updatedObject);
        }

        checkForDuplicates();

        write();
    }

    public void update(E updatedObject) throws MergeException {
        E existingObject = find(updatedObject);

        if (null == existingObject) {
            logger.error("Could not find match for update");
        } else {
            existingObject.merge(updatedObject);
        }

        write();
    }

    public E findMatch(E object) {
        for (E candidate : getData()) {
            if (object.isEquivalentTo(candidate))
                return candidate;
        }

        return null;
    }

    public void removeObjects(List objects) {
        List<E> oldObjects = (List<E>) objects;
        List<E> existingObjects = new ArrayList<E>(objects.size());

        for (E object : oldObjects) {
            E match = findMatch(object);
            if (null == match) {
                logger.error("No match for " + object);
            } else {
                existingObjects.add(match);
            }
        }

        getData().removeAll(existingObjects);

        write();
    }

    public E find(E object) {
        for (E candidate : getData()) {
            if (candidate.isEquivalentTo(object))
                return candidate;
        }

        return null;
    }
}
