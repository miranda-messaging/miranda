package com.ltsllc.miranda.messagesFile;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.cluster.Cluster;
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
public class SystemMessages extends MultipleFiles {
    private static SystemMessages ourInstance;

    private SystemMessages (String directory, BlockingQueue<Message> writerQueue)
    {
        super(directory, writerQueue);
    }

    public static synchronized void initialize (String directory, BlockingQueue witerQueue)
    {
        if (null == ourInstance) {
            ourInstance = new SystemMessages(directory, witerQueue);
        }
    }

    public static synchronized SystemMessages getInstance () {
        return ourInstance;
    }

    public void load ()
    {
        File f = new File(getDirectoryName());
        String[] contents = f.list();
        for (String s : contents) {
            if (isMessageFile(s)) {
                MessagesFile mf = new MessagesFile(s, getWriterQueue());
                mf.load();
            }
        }
    }

    public boolean isMessageFile (String s) {
        return false;
        
    }
}
