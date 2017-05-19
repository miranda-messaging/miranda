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

import com.google.gson.Gson;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.*;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/10/2017.
 */
abstract public class SingleFileReadyState<E> extends MirandaFileReadyState {
    abstract public Type getListType();
    abstract public String getName();

    private static Logger logger = Logger.getLogger(SingleFileReadyState.class);
    private static Gson ourGson = new Gson();


    public SingleFileReadyState (SingleFile file) {
        super(file);
    }

    public SingleFile getFile () {
        return (SingleFile) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case Load: {
                LoadMessage loadMessage = (LoadMessage) message;
                nextState = processLoadMessage(loadMessage);
                break;
            }

            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponseMessage (getFileResponseMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage(getFileMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case WriteSucceeded: {
                getFile().setDirty(false);
                break;
            }

            case Stop: {
                StopMessage stopMessage = (StopMessage) message;
                nextState = processStopMessage(stopMessage);
                break;
            }

            case AddObjects: {
                AddObjectsMessage addObjectsMessage = (AddObjectsMessage) message;
                nextState = processAddObjectsMessage(addObjectsMessage);
                break;
            }

            case UpdateObjects: {
                UpdateObjectsMessage updateObjectsMessage = (UpdateObjectsMessage) message;
                nextState = processUpdateObjectsMessage(updateObjectsMessage);
                break;
            }

            case RemoveObjects: {
                RemoveObjectsMessage removeObjectsMessage = (RemoveObjectsMessage) message;
                nextState = processRemoveObjectsMessage(removeObjectsMessage);
                break;
            }

            default :
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processGetFileResponseMessage (GetFileResponseMessage getFileResponseMessage) {
        String hexString = getFileResponseMessage.getContents();

        try {
            byte[] buffer = Utils.hexStringToBytes(hexString);
            String json = new String(buffer);
            List list = ourGson.fromJson(json, getListType());
            merge(list);
            write();
        } catch (IOException e) {
            Panic panic = new Panic("Excepion loading file", e, Panic.Reasons.ExceptionLoadingFile);
            Miranda.getInstance().panic(panic);
        }

        return getFile().getCurrentState();
    }

    public void merge (List list) {
        List<E> newList = (List<E>) list;
        for (E e : newList) {
            if (!contains(e))
                add(e);
        }
    }

    private State processGetFileMessage(GetFileMessage getFileMessage) {
        GetFileResponseMessage getFileResponseMessage = null;

        if (null == getFile().getData()) {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(),this, getFileMessage.getFilename());
        }
        else {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getFileMessage.getFilename(), getFile().getBytes());
        }

        send (getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getFile().performGarbageCollection();
        return this;
    }

    private State processLoadMessage (LoadMessage loadMessage) {
        getFile().load();
        LoadResponseMessage loadResponseMessage = new LoadResponseMessage(getFile().getQueue(), this, getFile().getData());
        loadMessage.reply(loadResponseMessage);

        return this;
    }

    public State processStopMessage (StopMessage stopMessage) {
        if (getFile().isDirty())
            getFile().getWriter().sendWrite(getFile().getQueue(), this, getFile().getFilename(), getFile().getBytes());

        SingleFileStoppingState singleFileStoppingState = new SingleFileStoppingState(getFile());
        return singleFileStoppingState;
    }

    public State processAddObjectsMessage (AddObjectsMessage addObjectsMessage) {
        getFile().addObjects(addObjectsMessage.getObjects());

        return getFile().getCurrentState();
    }

    public State processUpdateObjectsMessage (UpdateObjectsMessage updateObjectsMessage) {
        getFile().updateObjects(updateObjectsMessage.getUpdatedObjects());

        return getFile().getCurrentState();
    }

    public State processRemoveObjectsMessage (RemoveObjectsMessage removeObjectsMessage) {
        getFile().removeObjects(removeObjectsMessage.getObjects());

        return getFile().getCurrentState();
    }

    public void write() {
        byte[] buffer = getFile().getBytes();
        WriteMessage writeMessage = new WriteMessage(getFile().getFilename(), buffer, getFile().getQueue(), this);
        send(getFile().getWriterQueue(), writeMessage);
    }

    public Version getVersion () {
        return getFile().getVersion();
    }

    public boolean contains (Object o) {
        E e = (E) o;
        return getFile().contains(e);
    }

    public void add (E element) {
        getFile().getData().add(element);
    }
}
