package com.ltsllc.miranda;

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
    abstract public E convert (F f);

    private SingleFile<F> file;
    private List<E> data;

    public SingleFile<F> getFile() {
        return file;
    }

    public void setFile (SingleFile<F> file) {
        this.file = file;
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

    public Manager (String name, SingleFile<F> singleFile) {
        super(name);

        this.file = singleFile;

        FileLoadedMessage fileLoadedMessage = new FileLoadedMessage(null, this);
        this.file.addSubscriber(getQueue(), fileLoadedMessage);
        this.file.start();

        List<E> newList = new ArrayList<E>();
        this.data = newList;
    }

    public void performGarbageCollection () {
        getFile().sendGarbageCollectionMessage(getQueue(), this);
    }
}
