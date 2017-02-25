package com.ltsllc.miranda.test;

import java.io.File;

/**
 * Created by Clark on 2/24/2017.
 */
public class DirectoryCreator implements FileCreator {
    public boolean createFile(File file) {
        return file.mkdirs();
    }
}
