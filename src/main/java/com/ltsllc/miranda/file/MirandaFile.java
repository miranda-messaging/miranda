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
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.deliveries.Comparer;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.file.messages.WatchMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;
import org.apache.log4j.Logger;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/5/2017.
 */
abstract public class MirandaFile extends Consumer implements Comparer {
    abstract public void load();
    abstract public byte[] getBytes();

    private static Logger logger = Logger.getLogger(MirandaFile.class);
    private static Gson ourGson = new Gson();

    private String filename;
    private Writer writer;
    private Reader reader;
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
        return writer.getQueue();
    }

    public Writer getWriter() {
        return writer;
    }

    public List<Perishable> getElements() {
        return elements;
    }

    public Version getVersion() {
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

    public MirandaFile () {}

    public MirandaFile(String filename, Reader reader, Writer writer) {
        super("file");

        this.filename = filename;
        this.writer = writer;
        this.reader = reader;
        this.lastCollection = -1;
    }


    public String getFilename() {
        return filename;
    }

    public void write(String filename, byte[] array) {
        getWriter().sendWrite(getQueue(), this, filename, array);
    }

    public void write() {
        write(getFilename(), getBytes());
    }

    public void fileChanged() {
        logger.info(getFilename() + " changed");
        load();
    }

    public void watch() {
        File file = new File(getFilename());
        FileChangedMessage fileChangedMessage = new FileChangedMessage(getQueue(), this, file);
        WatchMessage message = new WatchMessage(getQueue(), this, file, fileChangedMessage);
        send(message, Miranda.fileWatcher.getQueue());
    }

    public void updateVersion() throws NoSuchAlgorithmException {
        String json = asJson();

        Version version = new Version(json);
        setVersion(version);
    }

    public String asJson () {
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

    public void performGarbageCollection () {
        setLastCollection(System.currentTimeMillis());
    }

    public void sendGarbageCollectionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(senderQueue, sender);
        sendToMe(garbageCollectionMessage);
    }
}
