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

package com.ltsllc.miranda.manager;

import com.google.gson.Gson;
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.clientinterface.requests.Files;
import com.ltsllc.miranda.directory.MirandaDirectory;
import com.ltsllc.miranda.event.EventDirectory;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * A directory of files that have to manged
 */
public abstract class DirectoryManager<T extends MirandaFile> extends Consumer {

    private int BUFFER_SIZE = 8192;

    private Reader reader;
    private Writer writer;
    private MirandaDirectory directory;
    private Map<String, String> map;
    private Version version;

    public MirandaFile getFile () {
        return directory;
    }
    public MirandaFile getFile (String file) throws MirandaException {
        String contenets = "";
        String fullName = directory.getFilename() + "/" + file;
        if (!map.containsKey(file)) {
            try {
                FileReader fileReader = new FileReader(fullName);
                int bytesRead;
                char[] buffer = new char[BUFFER_SIZE];
                do {
                    bytesRead = fileReader.read(buffer);
                    if (bytesRead != -1) {
                        contenets = contenets + buffer;
                    }
                } while (bytesRead == BUFFER_SIZE);
                map.put(file, contenets);
            } catch (IOException e) {
                throw new MirandaException("error trying to read " + fullName, e);
            }
        }
        String contents = map.get(file);
        Gson gson = new Gson();
        List list = gson.fromJson(contenets, List.class);
        return new MirandaFile() {
            @Override
            public byte[] getBytes() {
                return contents.getBytes();
            }

            @Override
            public List getData() {
                return list;
            }
        };
    }
    public Map<String, String> getMap() {
        return map;
    }

    public MirandaDirectory getDirectory() {
        return directory;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Version getVersion() throws GeneralSecurityException {
        if (null == version)
        {
            version = getDirectory().getVersion();
        }

        return version;
    }


    public void setDirectory(MirandaDirectory directory) {
        this.directory = directory;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public DirectoryManager(String name, String directory, int objectLimit, Reader reader, Writer writer) throws IOException, MirandaException {
        super(name);

        this.directory = new EventDirectory(directory, objectLimit, reader, writer);
        this.reader = reader;
        this.writer = writer;
        this.map = new HashMap<String, String>();
    }

    public void add(String key, String value) {
        getMap().put(key, value);
    }

    public void fileChanged(Map<String, Event> map) {
        map = new HashMap<String, Event>(map);
    }

    public void sendRefresh (BlockingQueue<Message> sederQueue, Object sender) {
        getDirectory().sendRefresh(getQueue(), sender);
    }


}
