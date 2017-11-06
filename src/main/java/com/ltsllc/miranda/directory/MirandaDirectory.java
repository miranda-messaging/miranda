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

package com.ltsllc.miranda.directory;

import com.google.gson.Gson;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.DirectoryEntry;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Clark on 5/13/2017.
 */
abstract public class MirandaDirectory<T extends DirectoryEntry> extends MirandaFile {
    abstract public boolean isInteresting(String name);
    abstract public Type getListType();

    private File directory;
    private List<File> files;
    private Map<String, T> map;
    private int objectLimit;
    private static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }

    public int getObjectLimit() {
        return objectLimit;
    }

    public Map<String, T> getMap() {
        return map;
    }

    public File getDirectory() {
        if (null == directory && null != getFilename())
            directory = new File(getFilename());

        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setDirectoryName(String directoryName) {
        directory = new File(directoryName);
    }

    public String getDirectoryName() {
        return directory.getName();
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public MirandaDirectory(String directoryName, int objectLimit, Reader reader, Writer writer) throws IOException, MirandaException {
        super(directoryName, reader, writer);
        this.directory = new File(directoryName);
        this.files = new ArrayList<File>();
        this.map = new HashMap<String, T>();
        this.objectLimit = objectLimit;

        setDirectoryName(directoryName);

        DirectoryStartState directoryStartState = new DirectoryStartState(this);
        setCurrentState(directoryStartState);

        load();
    }

    public void start() {
        ScanTask scanTask = new ScanTask(this);
        scanTask.start();
    }

    public void add(T item) {
        String key = item.getKey();
        getMap().put(key, item);
    }

    public void sendScanCompleteMessage(List<File> files) {
        ScanCompleteMessage scanCompleteMessage = new ScanCompleteMessage(null, this, files);
        sendToMe(scanCompleteMessage);
    }

    public void sendExceptionDuringScanMessage(Throwable throwable) {
        ExceptionDuringScanMessage exceptionDuringScanMessage = new ExceptionDuringScanMessage(null, this, throwable);
        sendToMe(exceptionDuringScanMessage);
    }

    public void load() {
        List<String> list = scan();

        for (String filename : list) {
            getReader().sendReadMessage(getQueue(), this, filename);
        }
    }

    public List<String> scan() {
        String filename = getDirectory().getName();

        try {
            filename = getDirectory().getCanonicalPath();
            List<String> list = new ArrayList<String>();
            scan (filename, list);
            return list;
        } catch (IOException e) {
            String message = "Exception during scan of " + filename;
            Panic panic = new Panic(message, e, Panic.Reasons.ExceptionDuringScan);
            Miranda.panicMiranda(panic);
            return new ArrayList<String>();
        }
    }

    public void scan(String filename, List<String> list) {
        File file = new File(filename);

        if (file.isDirectory()) {
            scanDirectory(filename, list);
        } else if (file.exists() && isInteresting(filename)) {
            list.add(filename);
        }
    }

    public void scanDirectory(String directoryName, List<String> list) {
        try {
            File directory = new File(directoryName);
            if (!directory.isDirectory())
                return;

            String canonicalPath = directory.getCanonicalPath();
            String[] contents = directory.list();
            for (String entry : contents) {
                String fullName = canonicalPath + File.pathSeparator + entry;
                scan(fullName, list);
            }
        } catch (IOException e) {
            String message = "Exception during scan of " + directoryName;
            Panic panic = new Panic(message, e, Panic.Reasons.ExceptionDuringScan);
            Miranda.panicMiranda(panic);
        }
    }

    public void fileLoaded(String filename, byte[] data) {
        String json = new String(data);
        List<T> list = new ArrayList<T>();

        if (getMap().size() >= getObjectLimit())
            return;

        list = getGson().fromJson(json, getListType());
        File file = new File(filename);
        Miranda.fileWatcher.sendWatchDirectoryMessage(getQueue(), this, file, getQueue());

        for (T item : list) {
            add(item);
        }
    }
}




