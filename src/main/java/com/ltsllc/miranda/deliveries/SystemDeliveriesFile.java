package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.file.MirandaFile;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */

/**
 * All the deliveries that the system knows about.
 */
public class SystemDeliveriesFile extends Directory {
    public SystemDeliveriesFile (String filename, BlockingQueue<Message> writerQueue) {
        super(filename, writerQueue);

        SystemDeliveriesFileReadyState readyState = new SystemDeliveriesFileReadyState(this);
        setCurrentState(readyState);
    }

    @Override
    public boolean isFileOfInterest(String filename) {
        return filename.endsWith("msg");
    }


    @Override
    public MirandaFile createMirandaFile(String filename) {
        return new DeliveriesFile(filename, getWriterQueue());
    }
}
