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
import com.ltsllc.commons.util.HexConverter;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.clientinterface.basicclasses.MergeException;
import com.ltsllc.miranda.clientinterface.basicclasses.Version;
import com.ltsllc.miranda.cluster.messages.LoadMessage;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.AddObjectsMessage;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.file.messages.RemoveObjectsMessage;
import com.ltsllc.miranda.file.messages.UpdateObjectsMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.StopMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;
import com.ltsllc.miranda.reader.ReadResponseMessage;
import com.ltsllc.miranda.shutdown.ShutdownMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/10/2017.
 */
public class SingleFileReadyState extends MirandaFileReadyState {
    private static Logger logger = Logger.getLogger(SingleFileReadyState.class);
    private static Gson ourGson = new Gson();

    public SingleFileReadyState () {

    }

    public SingleFileReadyState(SingleFile file) throws MirandaException {
        super(file);
    }

    public SingleFile getFile() {
        return (SingleFile) getContainer();
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

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

            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponseMessage(getFileResponseMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage(getFileMessage);
                break;
            }

            case Write: {
                WriteMessage writeMessage = (WriteMessage) message;
                nextState = processWriteMessage(writeMessage);
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

            case Shutdown: {
                ShutdownMessage shutdownMessage = (ShutdownMessage) message;
                nextState = processShutdownMessage(shutdownMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processGetFileResponseMessage(GetFileResponseMessage getFileResponseMessage) {
        try {
            getFile().setData(getFileResponseMessage.getContentAsBytes());
            getFile().getWriter().sendWrite(getFile().getQueue(), this, getFile().getFilename(),
                    getFile().getBytes());
            return new SingleFileWritingState(getFile(),this);
        } catch (IOException e) {
            Panic panic = new Panic("Exception trying to set data", e, Panic.Reasons.ExceptionSettingData);
            Miranda.panicMiranda(panic);
        }

        return this;
    }

    private State processGetFileMessage(GetFileMessage getFileMessage) {
        GetFileResponseMessage getFileResponseMessage = null;

        if (null == getFile().getData()) {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this);
        } else {
            getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getFile().getBytes());
        }

        send(getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }


    private State processLoadMessage(LoadMessage loadMessage) throws MirandaException {
        getFile().getReader().read(getFile().getFilename());
        SingleFileReadingState singleFileReadingState = new SingleFileReadingState(getFile(), this);
        singleFileReadingState.addLoaderListener(loadMessage.getSender());

        return singleFileReadingState;
    }

    public State processStopMessage(StopMessage stopMessage) throws MirandaException {
        if (getFile().isDirty())
            getFile().getWriter().sendWrite(getFile().getQueue(), this, getFile().getFilename(), getFile().getBytes());

        SingleFileShutdownState singleFileShutdownState = new SingleFileShutdownState(stopMessage.getSender());
        return singleFileShutdownState;
    }

    public State processAddObjectsMessage(AddObjectsMessage addObjectsMessage) {
        getFile().addObjects(addObjectsMessage.getObjects());

        return getFile().getCurrentState();
    }

    public State processUpdateObjectsMessage(UpdateObjectsMessage updateObjectsMessage) {
        try {
            getFile().updateObjects(updateObjectsMessage.getUpdatedObjects());

            return getFile().getCurrentState();
        } catch (MergeException e) {
            Panic panic = new Panic("Exception while trying to update objects", e,
                    Panic.Reasons.ExceptionDuringUpdate);
            Miranda.panicMiranda(panic);
        }

        return getFile().getCurrentState();
    }

    public State processRemoveObjectsMessage(RemoveObjectsMessage removeObjectsMessage) {
        getFile().removeObjects(removeObjectsMessage.getObjects());

        return getFile().getCurrentState();
    }

    public void write() {
        byte[] buffer = getFile().getBytes();
        WriteMessage writeMessage = new WriteMessage(getFile().getFilename(), buffer, getFile().getQueue(), this);
        send(getFile().getWriterQueue(), writeMessage);
    }

    public Version getVersion() {
        return getFile().getVersion();
    }

    public State processReadResponseMessage(ReadResponseMessage readResponseMessage) {
        switch (readResponseMessage.getResult()) {
            case Success: {
                processReadSuccess(readResponseMessage.getData());
                break;
            }

            case FileDoesNotExist: {
                processFileDoesNotExist();
                break;
            }

            case ExceptionReadingFile: {
                processExceptionReadingFile();
                break;
            }

            default: {
                Panic panic = new Panic("Unrecognized result reading file", Panic.Reasons.UnrecognizedResult);
                Miranda.panicMiranda(panic);
            }
        }

        return getFile().getCurrentState();
    }


    public void processReadSuccess(byte[] data) {
        getFile().setData(data);
        fireFileLoaded();
    }

    public void processFileDoesNotExist() {
        byte[] data = null;
        getFile().setData(data);
        fireFileLoaded();
    }

    public void processExceptionReadingFile() {
        byte[] data = null;
        getFile().setData(data);
        fireFileLoaded();
    }

    public State processWriteMessage (WriteMessage writeMessage) {
        SingleFileWritingState writingState = new SingleFileWritingState(getFile(), this);

        return writingState;
    }
}
