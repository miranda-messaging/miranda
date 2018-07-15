package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.State;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.manager.DirectoryManager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.panics.Panic;

public class DirectoryManagerStartState extends State {
    public DirectoryManager getDirectoryManager () {
        return (DirectoryManager) getContainer();
    }

    public DirectoryManagerStartState (DirectoryManager directoryManager) {
        super(directoryManager);
    }

    public State start () {
        try {
            Miranda.getInstance().getReader().sendScan(getDirectoryManager().getDirectory().getFilename(),
                    getDirectoryManager().getQueue(), getDirectoryManager());

            return new DirectoryManagerReadyState(getDirectoryManager());
        } catch (Exception e) {
            Panic panic = new Panic("Exception while trying to scan directory", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return getDirectoryManager().getCurrentState();
        }
    }

}
