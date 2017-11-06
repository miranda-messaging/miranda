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
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.Directory;
import com.ltsllc.miranda.file.MirandaFile;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import org.apache.log4j.Logger;

/**
 * Created by Clark on 2/19/2017.
 */
public class DirectoryReadyState extends State {
    private static Logger logger = Logger.getLogger(FileReadyState.class);

    private Directory directory;

    public DirectoryReadyState(Directory directory) throws MirandaException {
        super(directory);

        this.directory = directory;
    }

    public Directory getDirectory() {
        return directory;
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

            default :
                nextState = super.processMessage(message);
                break;
        }
        return nextState;
    }


    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        logger.info ("Garbage collecting " + getDirectory().getFilename());

        for (MirandaFile mirandaFile : getDirectory().getFiles())
        {
            mirandaFile.performGarbageCollection();
        }

        getDirectory().setLastCollection(System.currentTimeMillis());

        return this;
    }
}
