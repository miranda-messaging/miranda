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

package com.ltsllc.miranda.reader;

import com.ltsllc.miranda.message.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;

/**
 * Created by Clark on 5/3/2017.
 */
public class ReaderReadyState extends State {
    public Reader getReader() {
        return (Reader) getContainer();
    }

    public ReaderReadyState(Reader reader) throws MirandaException {
        super(reader);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getReader().getCurrentState();

        switch (message.getSubject()) {
            case Read: {
                ReadMessage readMessage = (ReadMessage) message;
                nextState = processReadMessage(readMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadMessage(ReadMessage readMessage) throws MirandaException {
        Reader.ReadResult readResult = getReader().read(readMessage.getFilename());
        ReadResponseMessage response = new ReadResponseMessage(readResult.result, getReader().getQueue(), getReader());
        response.setFilename(readMessage.getFilename());
        response.setData(readResult.data);
        response.setException(readResult.exception);
        response.setFilename(readResult.filename);
        response.setResult(readResult.result);
        readMessage.reply(response);

        return getReader().getCurrentState();
    }
}
