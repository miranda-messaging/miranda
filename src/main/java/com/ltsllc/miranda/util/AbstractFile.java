package com.ltsllc.miranda.util;

import java.io.File;

/**
 * Basically a jana.io.File that has some extra capabilities.
 */
public class AbstractFile {
    private String name;
    private String path;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean exists () {
        File file = new File(getPath(), getName());

        return file.exists();
    }
}
