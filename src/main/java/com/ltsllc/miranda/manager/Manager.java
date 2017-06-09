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
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.Updateable;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/26/2017.
 */
public abstract class Manager<E, F extends Updateable<F> & Matchable<F>> extends Consumer {
    abstract public SingleFile<F> createFile (String filename) throws IOException;
    abstract public State createStartState ();
    abstract public E convert (F f);

    private SingleFile<F> file;
    private List<E> data;
    private boolean testMode;

    public boolean getTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public SingleFile<F> getFile() {
        return file;
    }

    public void setFile (SingleFile<F> file) {
        if (this.file != null)
            this.file.removeSubscriber(getQueue());

        this.file = file;

        if (this.file != null) {
            FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this, null);
            this.file.addSubscriber(getQueue());
        }
    }

    public List<E> getData() {
        return data;
    }

    public void setData (List<E> data) {
        this.data = data;
    }

    public List<E> convertList (List<F> data) {
        ArrayList<E> newList = new ArrayList<E>();

        for (F f : data) {
            E e = convert(f);
            newList.add(e);
        }

        return newList;
    }

    public Manager (String name, String filename) throws IOException {
        super(name);

        SingleFile<F> file = createFile(filename);
        file.addSubscriber(getQueue());
        setFile(file);
        file.start();

        State startState = createStartState();
        setCurrentState(startState);

        List<E> newList = new ArrayList<E>();
        this.data = newList;
    }

    public Manager (String name, boolean testMode) {
        super(name);

        State startState = createStartState();
        setCurrentState(startState);

        List<E> newList = new ArrayList<E>();
        this.data = newList;
    }

    public void sendGarbageCollectionMessage (BlockingQueue<Message> senderQueue, Object sender) {
        GarbageCollectionMessage garbageCollectionMessage = new GarbageCollectionMessage(senderQueue, sender);
        sendToMe(garbageCollectionMessage);
    }

    public void performGarbageCollection () {
        getFile().sendGarbageCollectionMessage(getQueue(), this);
    }

    public void load () {
        getFile().sendLoad(getQueue(), this);
    }
}
