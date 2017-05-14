package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.Matchable;
import com.ltsllc.miranda.file.Updateable;

/**
 * Created by Clark on 5/14/2017.
 */
abstract public class StandardManager<E extends Updateable<E> & Matchable<E>> extends Manager<E, E> {
    public StandardManager (String name, String filename) {
        super (name, filename);
    }

    public E convert (E e) {
        return e;
    }
}
