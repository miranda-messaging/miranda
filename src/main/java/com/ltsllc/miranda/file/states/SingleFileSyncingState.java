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
import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.messages.GetFileResponseMessage;
import com.ltsllc.miranda.node.messages.GetFileMessage;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
abstract public class SingleFileSyncingState extends State {
    abstract public Type getListType();
    abstract public State getReadyState() throws MirandaException;
    abstract public List getData();
    abstract public boolean contains(Object o);
    abstract public SingleFile getFile();
    abstract public String getName();

    private static Gson ourGson = new Gson();

    public SingleFileSyncingState (Consumer consumer) throws MirandaException {
        super(consumer);
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = this;

        switch (message.getSubject()) {
            case GetFileResponse: {
                GetFileResponseMessage getFileResponseMessage = (GetFileResponseMessage) message;
                nextState = processGetFileResponse (getFileResponseMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage (getFileMessage);
                break;
            }

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    public State processGetFileResponse (GetFileResponseMessage getFileResponseMessage) {
        List list = ourGson.fromJson(getFileResponseMessage.getContents(), getListType());
        getFile().merge(list);

        return this;
    }

    private State processGetFileMessage (GetFileMessage getFileMessage) throws MirandaException {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getFile().getQueue(), this, getName(),
                getFile().getBytes());

        getFileMessage.reply(getFileResponseMessage);

        return this;
    }
}
