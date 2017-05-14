package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.directory.ExceptionDuringScanMessage;
import com.ltsllc.miranda.directory.MirandaDirectory;
import com.ltsllc.miranda.directory.ScanCompleteMessage;
import com.ltsllc.miranda.event.EventDirectory;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

import java.io.File;
import java.util.List;

/**
 * Created by Clark on 5/3/2017.
 */
public class DirectoryManager extends Consumer {
    private Reader reader;
    private Writer writer;
    private MirandaDirectory directory;

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

    public DirectoryManager (String name, String directory, Reader reader, Writer writer) {
        super (name);

        this.directory = new EventDirectory(directory);
        this.reader = reader;
        this.writer = writer;
    }

}
