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
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;

import java.util.List;

/**
 * Created by Clark on 5/14/2017.
 */
abstract public class ManagerLoadingState extends State {
    abstract public State getReadyState() throws MirandaException;

    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerLoadingState (Manager manager) throws MirandaException {
        super(manager);
    }

    public State processMessage (Message message) throws MirandaException {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage (fileLoadedMessage);
                break;
            }

            case FileDoesNotExist: {
                FileDoesNotExistMessage fileDoesNotExistMessage = (FileDoesNotExistMessage) message;
                nextState = processFileDoesNotExistMessage(fileDoesNotExistMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage (garbageCollectionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) throws MirandaException {
        List list = (List) fileLoadedMessage.getData();
        getManager().setData(list);

        return getReadyState();
    }

    public State processFileDoesNotExistMessage (FileDoesNotExistMessage fileDoesNotExistMessage) throws MirandaException {
        getManager().getData().clear();

        return getReadyState();
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        defer(garbageCollectionMessage);

        return getManager().getCurrentState();
    }
}
