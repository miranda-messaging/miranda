package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Version;

/**
 * Created by Clark on 2/6/2017.
 */
public class NameVersion {
    private String name;
    private Version version;

    public NameVersion (String name, Version version) {
        this.name = name;
        this.version = version;
    }


    public String getName() {
        return name;
    }

    public Version getVersion() {
        return version;
    }
}
