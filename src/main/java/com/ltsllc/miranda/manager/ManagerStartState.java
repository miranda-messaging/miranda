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
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;

import java.util.List;

/**
 * Created by Clark on 5/14/2017.
 */
abstract public class ManagerStartState extends State {
    abstract public State getReadyState();

    public Manager getManager () {
        return (Manager) getContainer();
    }

    public ManagerStartState (Manager manager) {
        super(manager);
    }

    public State processMessage (Message message) {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage(fileLoadedMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage (garbageCollectionMessage);
                break;
            }

            case FileDoesNotExist: {
                FileDoesNotExistMessage fileDoesNotExistMessage = (FileDoesNotExistMessage) message;
                nextState = processFileDoesNotExistMessage(fileDoesNotExistMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processFileLoadedMessage (FileLoadedMessage fileLoadedMessage) {
        List list = (List) fileLoadedMessage.getData();
        getManager().setData(list);

        restoreDeferredMessages();

        return getReadyState();
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        defer(garbageCollectionMessage);

        return getManager().getCurrentState();
    }

    public State processFileDoesNotExistMessage (FileDoesNotExistMessage fileDoesNotExistMessage) {
        getManager().getData().clear();

        restoreDeferredMessages();

        return getReadyState();
    }
}
