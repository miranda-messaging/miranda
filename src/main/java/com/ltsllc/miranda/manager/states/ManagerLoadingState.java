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

package com.ltsllc.miranda.manager.states;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.ReadResponseMessage;
import com.ltsllc.miranda.timer.messages.CancelMessage;
import com.ltsllc.miranda.timer.messages.TimeoutMessage;

import java.util.List;

/**
 * Created by Clark on 5/14/2017.
 */
public class ManagerLoadingState extends ManagerState {
    private State readyState;

    public State getReadyState() {
        return readyState;
    }

    public void setReadyState(State readyState) {
        this.readyState = readyState;
    }

    public ManagerLoadingState(Manager manager, State readyState) throws MirandaException {
        super(manager);
        setReadyState(readyState);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getManager().getCurrentState();

        switch (message.getSubject()) {
            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage(readResponseMessage);
                break;
            }

            case FileLoaded: {
                FileLoadedMessage fileLoadedMessage = (FileLoadedMessage) message;
                nextState = processFileLoadedMessage(fileLoadedMessage);
                break;
            }

            case FileDoesNotExist: {
                FileDoesNotExistMessage fileDoesNotExistMessage = (FileDoesNotExistMessage) message;
                nextState = processFileDoesNotExistMessage(fileDoesNotExistMessage);
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

    public State processFileLoadedMessage(FileLoadedMessage fileLoadedMessage) throws MirandaException {
        List list = (List) fileLoadedMessage.getData();
        getManager().setData(list);
        return getManager().getReadyState();
    }

    public State processFileDoesNotExistMessage(FileDoesNotExistMessage fileDoesNotExistMessage) throws MirandaException {
        getManager().getData().clear();
        return getManager().getReadyState();
    }

    public State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        defer(garbageCollectionMessage);

        return getManager().getCurrentState();
    }

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) throws MirandaException {
        if (readResponseMessage.getResult() == Results.Success) {
            CancelMessage cancelMessage = new CancelMessage (getManager().getQueue(), getManager(), getManager().getQueue());
            send(Miranda.timer.getQueue(), cancelMessage);

            return getManager().getReadyState();
        } else if (readResponseMessage.getResult() == Results.FileDoesNotExist) {
            getManager().getFile().sendCreateMessage(getManager().getQueue(), this);
            return new ManagerWritingState(getReadyState(), getManager());
        }

        return this;
    }

    public State start () {
        super.start();

        TimeoutMessage timeoutMessage = new TimeoutMessage(Miranda.timer.getQueue(), Miranda.timer);
        Miranda.timer.sendScheduleOnce(10000, getManager().getQueue(), timeoutMessage);

        getManager().getFile().sendLoad(getManager().getQueue(), getManager());

        return this;
    }

    public void exit () {
        Miranda.timer.sendCancel(getManager().getQueue(), getManager(), getManager().getQueue());
    }

}
