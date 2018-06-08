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
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.file.messages.*;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.ReadMessage;
import com.ltsllc.miranda.writer.WriteFailedMessage;
import com.ltsllc.miranda.writer.WriteSucceededMessage;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class FileReadyState extends State {
    private static Logger logger = Logger.getLogger(FileReadyState.class);

    private MirandaFile file;

    public BlockingQueue<Message> getInitiator() {
        return initiator;
    }

    public void setInitiator(BlockingQueue<Message> initiator) {
        this.initiator = initiator;
    }

    private BlockingQueue<Message> initiator;


    public FileReadyState(MirandaFile file) throws MirandaException {
        super(file);

        this.file = file;
    }


    public MirandaFile getFile() {
        return file;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case FileChanged: {
                FileChangedMessage fileChangedMessage = (FileChangedMessage) message;
                nextState = processFileChangedMessage(fileChangedMessage);
                break;
            }

            case WriteFile: {
                WriteFileMessage writeFileMessage = (WriteFileMessage) message;
                nextState = processWriteFileMessage(writeFileMessage);
                break;
            }

            case WriteSucceeded: {
                WriteSucceededMessage writeSucceededMessage = (WriteSucceededMessage) message;
                nextState = processWriteSucceededMessage(writeSucceededMessage);
                break;
            }

            case WriteFailed: {
                WriteFailedMessage writeFailedMessage = (WriteFailedMessage) message;
                nextState = processWriteFailedMessage(writeFailedMessage);
                break;
            }

            case Read: {
                ReadFileMessage readFileMessage = (ReadFileMessage) message;
                nextState = processReadFileMessage(readFileMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }
        return nextState;
    }

    /**
     * Someone asked us to read our file.
     * @param readFileMessage The request
     * @return The next state
     */
    private State processReadFileMessage(ReadFileMessage readFileMessage) {
        File file = new File(getFile().getFilename());
        getFile().getReader().sendReadMessage(getFile().getQueue(),this, getFile().getFilename());
        setInitiator(readFileMessage.getSender());
        return this;
    }

    public State processWriteFailedMessage(WriteFailedMessage writeFailedMessage) {
        if (null != getInitiator()) {
            WriteFileResponseMessage responseMessage = new WriteFileResponseMessage(getFile().getQueue(),
                    this, Results.Failure);
            send(getInitiator(), responseMessage);
            setInitiator(null);
        }

        return this;
    }

    public State processWriteFileMessage(WriteFileMessage writeFileMessage) {
        getFile().write();
        setInitiator(writeFileMessage.getSender());
        return this;
    }

    public State processWriteSucceededMessage (WriteSucceededMessage writeSucceededMessage) {
        if (null != getInitiator()) {
            WriteFileResponseMessage fileWrittenMessage = new WriteFileResponseMessage(getFile().getQueue(), this,
                    Results.Success);
            send(getInitiator(), fileWrittenMessage);
            setInitiator(null);
        }

        return this;
    }

    private State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        return getFile().getCurrentState();
    }


    public State processFileChangedMessage(FileChangedMessage fileChangedMessage) {
        getFile().load();

        return getFile().getCurrentState();
    }
}
