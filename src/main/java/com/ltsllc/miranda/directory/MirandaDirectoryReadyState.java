package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;

import java.io.IOException;

/**
 * Created by Clark on 6/7/2017.
 */
public class MirandaDirectoryReadyState extends State {
    private MirandaDirectory mirandaDirectory;

    public MirandaDirectory getMirandaDirectory() {
        return mirandaDirectory;
    }

    public void setMirandaDirectory(MirandaDirectory mirandaDirectory) {
        this.mirandaDirectory = mirandaDirectory;
    }

    public MirandaDirectoryReadyState(MirandaDirectory mirandaDirectory) throws MirandaException {
        super(mirandaDirectory);
    }

    public State processMessage(Message m) throws MirandaException{
        State nextState = getContainer().getCurrentState();

        try {
            switch (m.getSubject()){
                case Refresh : {
                    RefreshMessage refreshMessage = (RefreshMessage) m;
                    nextState = processRefreshMessage(refreshMessage);
                    break;
                }

                default:
                    super.processMessage(m);
                    break;
            }
        }
        catch (IOException e) {
            throw new MirandaException(e);
        }

        return nextState;
    }

    public State processRefreshMessage(RefreshMessage refreshMessage) throws MirandaException, IOException {
        MirandaDirectoryLoadingState directoryLoadingState =
                new MirandaDirectoryLoadingState(getMirandaDirectory());
        Miranda.getInstance().getReader().sendReadMessage(getMirandaDirectory().getQueue(),
                getMirandaDirectory(), getMirandaDirectory().getDirectory().getCanonicalFile());
        return directoryLoadingState;
    }

}
