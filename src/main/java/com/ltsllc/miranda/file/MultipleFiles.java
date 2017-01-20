package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Miranda;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
abstract public class MultipleFiles
{
    private static Logger logger = Logger.getLogger(MultipleFiles.class);

    private String directoryName;
    private BlockingQueue<Message> writerQueue;

    public MultipleFiles (String directoryName, BlockingQueue<Message> writerQueue)
    {
        this.writerQueue = writerQueue;
        setDirectoryName(directoryName);
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName (String s) {
        directoryName = s;
    }

    public BlockingQueue<Message> getWriterQueue () {
        return writerQueue;
    }

    public void fileChanged (String s) {
        logger.info (s + " changed");
    }


}
