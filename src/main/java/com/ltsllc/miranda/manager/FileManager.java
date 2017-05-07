package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

/**
 * Created by Clark on 5/3/2017.
 */
public class FileManager extends Consumer {
    private Reader reader;
    private Writer writer;

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public FileManager (String name, String filename, Reader reader, Writer writer) {
        super(name);

        this.reader = reader;
        this.writer = writer;
    }
}
