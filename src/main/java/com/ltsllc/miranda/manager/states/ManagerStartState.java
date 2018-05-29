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

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.FileDoesNotExistMessage;
import com.ltsllc.miranda.file.messages.FileLoadedMessage;
import com.ltsllc.miranda.manager.Manager;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;

import java.util.List;

/**
 * Created by Clark on 5/14/2017.
 */
public class ManagerStartState extends State {
    public Manager getManager() {
        return (Manager) getContainer();
    }

    public ManagerStartState(Manager manager) throws MirandaException {
        super(manager);
    }

    public State start () {
        try {
            return new ManagerLoadingState(getManager());
        } catch (MirandaException e) {
            Panic panic = new Panic("Exception starting actor", e, Panic.Reasons.Exception);
            Miranda.panicMiranda(panic);
            return this;
        }
    }
}
