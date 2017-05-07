package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.State;

/**
 * Created by Clark on 5/3/2017.
 */
public class ReaderReadyState extends State {
    public Reader getReader () {
        return (Reader) getContainer();
    }

    public ReaderReadyState (Reader reader) {
        super(reader);
    }
}
