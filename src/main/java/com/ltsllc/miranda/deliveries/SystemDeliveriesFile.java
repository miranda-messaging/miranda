package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.writer.Writer;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */

/**
 * All the deliveries that the system knows about.
 */
public class SystemDeliveriesFile extends Directory {
    public static final String FILE_NAME = "deliveries";

    public SystemDeliveriesFile (String filename, Writer writer) {
        super(filename, writer);

        SystemDeliveriesFileReadyState readyState = new SystemDeliveriesFileReadyState(this);
        setCurrentState(readyState);

        setInstance(this);
    }

    private static SystemDeliveriesFile ourInstance;

    public static SystemDeliveriesFile getInstance () {
        return ourInstance;
    }

    public static void setInstance (SystemDeliveriesFile systemDeliveriesFile) {
        ourInstance = systemDeliveriesFile;
    }

    @Override
    public boolean isFileOfInterest(String filename) {
        return filename.endsWith("msg");
    }


    @Override
    public MirandaFile createMirandaFile(String filename) {
        return new DeliveriesFile(filename, getWriter());
    }

    public void sendGetSystemDeliveries (BlockingQueue<Message> senderQueue, Object sender, String filename) {
        GetSystemDeliveriesMessage getSystemDeliveriesMessage = new GetSystemDeliveriesMessage(senderQueue, sender, filename);
        sendToMe(getSystemDeliveriesMessage);
    }
}
