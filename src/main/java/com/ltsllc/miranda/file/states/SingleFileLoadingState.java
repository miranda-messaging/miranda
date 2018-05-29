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
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.reader.ReadResponseMessage;

/**
 * Created by Clark on 5/14/2017.
 */
public class SingleFileLoadingState extends State {

    public SingleFile getSingleFile() {
        return (SingleFile) getContainer();
    }

    public SingleFileLoadingState(SingleFile singleFile) throws MirandaException {
        super(singleFile);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getSingleFile().getCurrentState();

        switch (message.getSubject()) {
            case ReadResponse: {
                ReadResponseMessage readResponseMessage = (ReadResponseMessage) message;
                nextState = processReadResponseMessage(readResponseMessage);
                break;
            }

            case FileChanged: {
                FileChangedMessage fileChangedMessage = (FileChangedMessage) message;
                nextState = processFileChangedMessage(fileChangedMessage);
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
        State nextState = getSingleFile().getCurrentState();

        if (readResponseMessage.getResult() == ReadResponseMessage.Results.Success) {
            getSingleFile().processData(readResponseMessage.getData());
            nextState = new SingleFileReadyState(getSingleFile());
            getSingleFile().fireFileLoaded();
        } else if (readResponseMessage.getResult() == ReadResponseMessage.Results.ExceptionReadingFile) {
            Panic panic = new Panic("Error trying to load file", readResponseMessage.getException(), Panic.Reasons.ErrorLoadingFile);
            Miranda.getInstance().panic(panic);
        } else {
            Panic panic = new Panic("Unrecogized result from reading " + readResponseMessage.getFilename(), Panic.Reasons.UnrecognizedResult);
            Miranda.panicMiranda(panic);
        }

        return nextState;
    }

    public State processFileChangedMessage(FileChangedMessage fileChangedMessage) {
        getSingleFile().getReader().sendReadMessage(getSingleFile().getQueue(), this, getSingleFile().getFilename());

        return getSingleFile().getCurrentState();
    }
}
