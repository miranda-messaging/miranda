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
import com.ltsllc.clcl.EncryptionException;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
abstract public class MirandaFile extends Consumer implements Comparer {
    abstract public byte[] getBytes();

    abstract public List getData();

    private static Logger logger = Logger.getLogger(MirandaFile.class);
    private static Gson ourGson = new Gson();

    private List<Subscriber> subscribers;
    private String filename;
    private Writer writer;
    private Reader reader;
    private List elements = new ArrayList();
    private Version version;
    private long lastLoaded = -1;
    private long lastCollection;
    private boolean dirty;


    public MirandaFile() {
    }

    public MirandaFile(String filename, Reader reader, Writer writer) throws IOException {
        basicConstructor(filename, reader, writer);
    }

    public long getLastCollection() {
        return lastCollection;
    }

    public void setLastCollection(long lastCollection) {
        this.lastCollection = lastCollection;
    }

    public BlockingQueue<Message> getWriterQueue() {
        return writer.getQueue();
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter (Writer writer) {
        this.writer = writer;
    }

    public List getElements() {
        return elements;
    }

    public Version getVersion() {
        if (null == version) {
            version = calculateVersion();
        }

        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public long getLastLoaded() {
        return lastLoaded;
    }

    public void setLastLoaded(long lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public static Gson getGson() {
        return ourGson;
    }


    public void recalculateVersion() {
        version = calculateVersion();
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void basicConstructor(String filename, Reader reader, Writer writer) throws IOException {
        super.basicConstructor("file");

        this.filename = filename;
        this.writer = writer;
        this.reader = reader;
        this.lastLoaded = -1;
        this.lastCollection = -1;
        this.subscribers = new ArrayList<Subscriber>();

        load();
    }


    public String getFilename() {
        return filename;
    }

    public void write(String filename, byte[] content) {
        getWriter().sendWrite(getQueue(), this, filename, content);
    }

    public void write() {
        write(getFilename(), getBytes());
    }

    public void fileChanged() throws IOException {
        logger.info(getFilename() + " changed");
        load();
    }

    public void watch() {
        File file = new File(getFilename());
        Miranda.fileWatcher.sendWatchFileMessage(getQueue(), this, file, getQueue());
    }

    public void updateVersion() throws Exception {
        String json = asJson();

        Version version = new Version(json);
        setVersion(version);
    }

    public String asJson() {
        return ourGson.toJson(getElements());
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (null == o || !(o instanceof MirandaFile))
            return false;

        MirandaFile other = (MirandaFile) o;

        return other.getVersion().equals(getVersion());
    }


    public boolean compare(Map<Object, Boolean> map, Object o) {
        if (map.containsKey(o))
            return map.get(o).booleanValue();

        if (o == this)
            return true;

        if (null == o || !(o instanceof MirandaFile)) {
            map.put(o, Boolean.FALSE);
            return false;
        }

        map.put(o, Boolean.TRUE);
        MirandaFile other = (MirandaFile) o;

        return getVersion().equals(other.getVersion());
    }

    public String toString() {
        return filename;
    }

    public void performGarbageCollection() {
        setLastCollection(System.currentTimeMillis());
    }

    public void sendGarbageCollectionMessage(BlockingQueue<Message> senderQueue, Object sender) {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(senderQueue, sender);
        sendToMe(garbageCollectionMessage);
    }

    public Version calculateVersion() {
        try {
            byte[] data = getBytes();
            return new Version(data);
        } catch (Exception e) {
            Panic panic = new Panic("Exception trying to calculate sha1", e, Panic.Reasons.ExceptionCalculatingSha1);
            Miranda.panicMiranda(panic);
        }

        return null;
    }

    public void fireMessage(Message message) {
        for (Subscriber subscriber : getSubscribers()) {
            subscriber.notifySubscriber(message);
        }
    }

    public void fireFileLoaded() {
        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(getQueue(), this, getData());
        fireMessage(fileLoadedMessage);
    }

    public void fireFileDoesNotExist() {
        FileDoesNotExistMessage fileDoesNotExistMessage = new FileDoesNotExistMessage(getQueue(), this,
                getFilename());
        fireMessage(fileDoesNotExistMessage);
    }

    public void load() {
        getReader().sendReadMessage(getQueue(), this, getFilename());
    }
}
