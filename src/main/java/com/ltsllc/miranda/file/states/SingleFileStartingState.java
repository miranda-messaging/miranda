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

package com.ltsllc.miranda.file.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.ReadResponseMessage;
import com.ltsllc.miranda.subsciptions.SubscriptionsFileReadyState;

/**
 * A state for when the object is first created.  It waits for the read to complete
 */
abstract public class SingleFileStartingState extends State {
    abstract public State getReadyState();

    public SingleFile getFile () {
        return (SingleFile) getContainer();
    }

    public SingleFileStartingState (SingleFile singleFile) {
        super(singleFile);
    }

    public State processMessage (Message message) {
        State nextState = getFile().getCurrentState();

        switch (message.getSubject()) {
            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage (readResponseMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) {
        if (readResponseMessage.getResult() == ReadResponseMessage.Results.Success) {
            getFile().setData(readResponseMessage.getData());
            getFile().fireFileLoaded();
            restoreDeferredMessages();
            return getReadyState();
        } else if (readResponseMessage.getResult() == ReadResponseMessage.Results.FileDoesNotExist) {
            getFile().getData().clear();
            getFile().fireFileDoesNotExist();
            restoreDeferredMessages();
            return getReadyState();
        } else {
            StartupPanic startupPanic = new StartupPanic("Problem loading " + getFile().getFilename(), null,
                    StartupPanic.StartupReasons.ProblemLoadingFile);
            Miranda.panicMiranda(startupPanic);

            return StopState.getInstance();
        }
    }

    public State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        defer(garbageCollectionMessage);

        return getFile().getCurrentState();
    }
}
