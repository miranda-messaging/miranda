package com.ltsllc.miranda.messagesFile;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.file.MirandaProperties;
import com.ltsllc.miranda.file.MultipleFiles;
import com.ltsllc.miranda.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/8/2017.
 */
public class SystemMessages extends Directory {
    public SystemMessages (String directory, BlockingQueue<Message> writerQueue)
    {
        super(directory, writerQueue);
    }

    @Override
    public boolean isFileOfInterest(String filename) {
        return filename.endsWith("msg");
    }

    @Override
    public MirandaFile createMirandaFile(String filename) {
        return new MessagesFile(filename, getWriterQueue());
    }
}
