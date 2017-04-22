package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.writer.Writer;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/26/2017.
 */
abstract public class PerishableFile<E extends Perishable & Updateable<E> & Matchable<E>> extends SingleFile<E> {
    public PerishableFile (String filename, Writer writer) {
        super(filename, writer);
    }
}
