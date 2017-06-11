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
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/19/2017.
 */
public class FileReadyState extends State {
    private static Logger logger = Logger.getLogger(FileReadyState.class);

    private MirandaFile file;


    public FileReadyState (MirandaFile file) {
        super(file);

        this.file = file;
    }


    public MirandaFile getFile() {
        return file;
    }

    @Override
    public State processMessage(Message message) {
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

            default :
                nextState = super.processMessage(message);
                break;
        }
        return nextState;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        long now = System.currentTimeMillis();

        List<Perishable> expired = new ArrayList<Perishable>();

        for (Perishable perishable : getFile().getElements()) {
            if (perishable.expired(now)) {
                expired.add(perishable);
            }
        }

        getFile().getElements().removeAll(expired);

        getFile().setLastCollection(now);

        return this;
    }


    public State processFileChangedMessage (FileChangedMessage fileChangedMessage) {
        try {
            getFile().load();
        } catch (IOException e) {
            Panic panic = new Panic("Exception loading file", e, Panic.Reasons.ErrorLoadingFile);
            Miranda.panicMiranda(panic);
        }

        return getFile().getCurrentState();
    }
}
