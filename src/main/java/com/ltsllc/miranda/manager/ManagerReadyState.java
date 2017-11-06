/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.ShutdownMessage;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;

import java.util.List;

/**
 * Created by Clark on 4/26/2017.
 */
public class ManagerReadyState<E,F> extends State {
    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerReadyState (Manager manager) throws MirandaException {
        super(manager);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case FileDoesNotExist: {
                FileDoesNotExistMessage fileDoesNotExistMessage = (FileDoesNotExistMessage) message;
                nextState = processFileDoesNotExistMessage(fileDoesNotExistMessage);
                break;
            }

            case FileChanged: {
                FileChangedMessage fileChangedMessage = (FileChangedMessage) message;
                nextState = processFileChangedMessage(fileChangedMessage);
                break;
            }

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage (shutdownMessage);
                break;
            }

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage(fileLoadedMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processShutdownMessage (ShutdownMessage shutdownMessage) throws MirandaException {
        ManagerShuttingDownState managerShuttingDownState = new ManagerShuttingDownState(getManager(),
                shutdownMessage.getSender());

        return managerShuttingDownState;
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) throws MirandaException {
        List<F> data = (List<F>) fileLoadedMessage.getData();
        List<E> newList = getManager().convertList(data);
        getManager().setData(newList);

        return getManager().getCurrentState();
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getManager().performGarbageCollection();

        return getManager().getCurrentState();
    }

    public State processFileDoesNotExistMessage (FileDoesNotExistMessage fileDoesNotExistMessage) {
        getManager().getData().clear();

        return getManager().getCurrentState();
    }

    public State processFileChangedMessage (FileChangedMessage fileChangedMessage) throws MirandaException {
        getManager().fileChanged();

        return getManager().getCurrentState();
    }
}
