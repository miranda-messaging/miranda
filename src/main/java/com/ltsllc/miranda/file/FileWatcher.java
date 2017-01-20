package com.ltsllc.miranda.file;

import org.apache.log4j.Logger;

import java.lang.reflect.Method;

/**
 * Created by Clark on 1/10/2017.
 */
public class FileWatcher {
    private Logger logger = Logger.getLogger(FileWatcher.class);

    public void watchForChanges (String filename, Object callbackInstance, Method callbackMethod) {
        logger.info("watchForChanges for filename: " + filename);
    }
}
