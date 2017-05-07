package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

/**
 * Created by Clark on 5/3/2017.
 */
public class DirectoryManager extends Consumer {
    private Reader reader;
    private Writer writer;

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

    public DirectoryManager (String name, String directory, Reader reader, Writer writer) {
        super (name);

        this.reader = reader;
        this.writer = writer;
    }
}
