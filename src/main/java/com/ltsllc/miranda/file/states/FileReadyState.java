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

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.*;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.reader.messages.ReadMessage;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 2/19/2017.
 */
public class FileReadyState extends State {
    private static Logger logger = Logger.getLogger(FileReadyState.class);

    private SingleFile file;

    public BlockingQueue<Message> getInitiator() {
        return initiator;
    }

    public void setInitiator(BlockingQueue<Message> initiator) {
        this.initiator = initiator;
    }

    private BlockingQueue<Message> initiator;


    public FileReadyState(SingleFile file) throws MirandaException {
        super(file);

        this.file = file;
    }


    public SingleFile getFile() {
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

            case Read: {
                ReadMessage readMessage = (ReadMessage) message;
                nextState = processReadMessage(readMessage);
                break;
            }

            case Write: {
                WriteMessage writeMessage = (WriteMessage) message;
                nextState = processWriteMessage(writeMessage);
                break;
            }
            default:
                nextState = super.processMessage(message);
                break;
        }
        return nextState;
    }

    public State processWriteMessage (WriteMessage writeMessage) {
        getFile().getWriter().sendWrite(getFile().getQueue(), getFile(), getFile().getFilename(), getFile().getBytes());
        return new SingleFileWritingState(getFile(), this);
    }

    private State processGarbageCollectionMessage(GarbageCollectionMessage garbageCollectionMessage) {
        return getFile().getCurrentState();
    }


    public State processFileChangedMessage(FileChangedMessage fileChangedMessage) {
        getFile().load();

        return getFile().getCurrentState();
    }

    public State processReadMessage (ReadMessage readMessage) throws MirandaException {
        return new SingleFileReadingState(getFile(), this);
    }
}
