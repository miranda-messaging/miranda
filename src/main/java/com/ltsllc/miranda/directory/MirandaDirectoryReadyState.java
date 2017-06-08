package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.State;

/**
 * Created by Clark on 6/7/2017.
 */
public class MirandaDirectoryReadyState extends State {
    public MirandaDirectoryReadyState (MirandaDirectory mirandaDirectory) {
        super(mirandaDirectory);
    }

    public MirandaDirectory getMirandaDirectory () {
        return (MirandaDirectory) getContainer();
    }


}
