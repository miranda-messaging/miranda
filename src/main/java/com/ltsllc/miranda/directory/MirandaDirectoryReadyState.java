package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 6/7/2017.
 */
public class MirandaDirectoryReadyState extends State {
    public MirandaDirectoryReadyState(MirandaDirectory mirandaDirectory) throws MirandaException {
        super(mirandaDirectory);
    }

    public MirandaDirectory getMirandaDirectory() {
        return (MirandaDirectory) getContainer();
    }


}
