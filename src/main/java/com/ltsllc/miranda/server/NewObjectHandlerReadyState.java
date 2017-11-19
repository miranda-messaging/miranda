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

package com.ltsllc.miranda.server;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.SingleFile;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;

/**
 * Created by Clark on 2/18/2017.
 */
abstract public class NewObjectHandlerReadyState<T extends SingleFile, W extends NewObjectPostHandler> extends State {
    private static Logger logger = Logger.getLogger(NewObjectHandlerReadyState.class);

    abstract public Type getBasicType();

    private W handler;
    private T file;

    public NewObjectHandlerReadyState(Consumer consumer, T file, W handler) throws MirandaException {
        super(consumer);

        this.file = file;
        this.handler = handler;
    }

    public T getFile() {
        return file;
    }

    public W getHandler() {
        return handler;
    }

    @Override
    public State processMessage(Message message) throws MirandaException {
        State nextState = getContainer().getCurrentState();

        switch (message.getSubject()) {
            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }
}
