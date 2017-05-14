package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.Updateable;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 4/26/2017.
 */
public abstract class Manager<E, F extends Updateable<F> & Matchable<F>> extends Consumer {
    abstract public SingleFile<F> createFile (String filename);
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
            this.file.addSubscriber(getQueue(), fileLoadedMessage);
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

    public Manager (String name, String filename) {
        super(name);

        SingleFile<F> file = createFile(filename);
        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(getQueue(), this, null);
        file.addSubscriber(getQueue(), fileLoadedMessage);
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

    public void performGarbageCollection () {
        getFile().sendGarbageCollectionMessage(getQueue(), this);
    }
}
