package com.ltsllc.miranda.deliveries;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

/**
 * Created by Clark on 5/1/2017.
 */
public class DeliveryManager extends DirectoryManager {
    public static final String NAME = "delivery manager";

    public DeliveryManager (String directory, Reader reader, Writer writer) {
        super(NAME, directory, reader, writer);
    }
}
