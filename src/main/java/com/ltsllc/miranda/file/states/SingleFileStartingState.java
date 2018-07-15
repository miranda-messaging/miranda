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

import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.CreateMessage;
import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.panics.Panic;
import com.ltsllc.miranda.panics.StartupPanic;
import com.ltsllc.miranda.reader.messages.ReadResponseMessage;


/**
 * A state for when the object is first created.  It waits for the read to complete
 */
abstract public class SingleFileStartingState extends State {
    abstract public State getReadyState () throws MirandaException;

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }

    public SingleFileStartingState(SingleFile singleFile) throws MirandaException {
        super(singleFile);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getFile().getCurrentState();

        switch (message.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) message;
                nextState = processLoadMessage(loadMessage);
                break;
            }

            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage(readResponseMessage);
                break;
            }

            case Create: {
                CreateMessage createMessage = (CreateMessage) message;
                nextState = processCreateMessage(createMessage);
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

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) throws MirandaException {
        if (readResponseMessage.getResult() == Results.Success) {
            getFile().setData(readResponseMessage.getData());
            getFile().fireFileLoaded();
            restoreDeferredMessages();
            return getReadyState();
        } else if (readResponseMessage.getResult() == Results.FileDoesNotExist) {
            getFile().getData().clear();
            getFile().fireFileDoesNotExist();
            restoreDeferredMessages();
            return this;
        } else if (readResponseMessage.getResult() == Results.ExceptionReadingFile) {
            StartupPanic startupPanic = new StartupPanic("Problem loading " + getFile().getFilename(),
                    readResponseMessage.getException(),
                    StartupPanic.StartupReasons.ProblemLoadingFile);
            Miranda.panicMiranda(startupPanic);

            return StopState.getInstance();
        } else if (readResponseMessage.getResult() == Results.ExceptionDecryptingFile) {
            StartupPanic startupPanic = new StartupPanic("Error decrypting " + getFile().getFilename(),
                    readResponseMessage.getException(),
                    StartupPanic.StartupReasons.ProblemLoadingFile);
            Miranda.panicMiranda(startupPanic);

            return StopState.getInstance();
        } else {
            StartupPanic startupPanic = new StartupPanic("Got unrecognized result: " + readResponseMessage.getResult(),
                    null, StartupPanic.StartupReasons.UnrecognizedResult);
            Miranda.panicMiranda(startupPanic);

            return StopState.getInstance();
        }
    }

    public State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        defer(garbageCollectionMessage);

        return getFile().getCurrentState();
    }

    private State processLoadMessage(LoadMessage loadMessage) {
        getFile().load();

        return this;
    }

    public State processCreateMessage (CreateMessage createMessage) throws MirandaException {
        getFile().getWriter().sendWrite(getFile().getQueue(), this, getFile().getName(), getFile().getBytes());
        return new SingleFileCreatingState (getReadyState(), getFile());
    }

    public State start () {
        try {
            getFile().getReader().sendReadMessage(getFile().getQueue(), this, getFile().getFilename());
            return new SingleFileReadingState(getFile(), getReadyState());
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception trying to star", e, Panic.Reasons.ExceptionTryingToStart);
            Miranda.panicMiranda(panic);
            return this;
        }
    }
}
