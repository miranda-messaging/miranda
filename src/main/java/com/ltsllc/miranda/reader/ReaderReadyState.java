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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Results;
import com.ltsllc.miranda.State;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Created by Clark on 5/3/2017.
 */
public class ReaderReadyState extends State {
    public Reader getReader () {
        return (Reader) getContainer();
    }

    public ReaderReadyState (Reader reader) {
        super(reader);
    }

    public State processMessage (Message message) {
        State nextState = getReader().getCurrentState();

        switch (message.getSubject()) {
            case Read : {
                ReadMessage readMessage = (ReadMessage) message;
                nextState = processReadMessage (readMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processReadMessage (ReadMessage readMessage) {
        Reader.ReadResult result = new Reader.ReadResult();

        try {
            Reader.ReadResult readResult = getReader().read(readMessage.getFilename());

            ReadResponseMessage response = new ReadResponseMessage(getReader().getQueue(), this, readResult.result,
                    result.data);
            readMessage.reply(response);
        } catch (GeneralSecurityException | IOException e) {
            result.result = Results.Exception;
            result.setAdditionalInfo(e);
            ReadResponseMessage response = new ReadResponseMessage(getReader().getQueue(), this, e);
        }

        return getReader().getCurrentState();
    }
}
