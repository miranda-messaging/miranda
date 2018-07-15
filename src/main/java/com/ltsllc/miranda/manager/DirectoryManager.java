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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.Equivalent;
import com.ltsllc.miranda.clientinterface.basicclasses.Event;
import com.ltsllc.miranda.clientinterface.basicclasses.Mergeable;
import com.ltsllc.miranda.directory.MirandaDirectory;
import com.ltsllc.miranda.event.EventDirectory;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Manager}-like class that manages a directory instead of one file.
 */
abstract public class DirectoryManager<T extends Mergeable & Equivalent> extends Consumer {
    /**
     * This method gets called when the directory is loaded.
     *
     * <p>
     *     The idea is for subclasses to convert an entry into whatever class the manager deals with
     * </p>
     *
     * @param string The entry to be processed.  This is expected to be a full filename for the directory entry
     */
    public abstract void processEntry(String string);

    /**
     * Return the ready state for this manager.
     *
     * <p>
     *     The idea is that the subclass will want to add the ability to process some messages that this
     *     class wouldn't understand.
     * </p>
     *
     * @return The "ready state" for this manager
     */
    public abstract State getReadyState();

    private Reader reader;
    private Writer writer;
    private MirandaDirectory directory;
    private Map<String, T> map;

    public Map<String, T> getMap() {
        return map;
    }

    public MirandaDirectory getDirectory() {
        return directory;
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
        this.map = new HashMap<String, T>();
    }

    public void add(String key, T value) {
        getMap().put(key, value);
    }

    public void fileChanged(Map<String, Event> map) {
        map = new HashMap<String, Event>(map);
    }

}
