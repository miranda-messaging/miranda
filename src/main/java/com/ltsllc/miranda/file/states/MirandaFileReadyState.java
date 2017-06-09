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
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.miranda.messages.GarbageCollectionMessage;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;

import java.io.IOException;

/**
 * Created by Clark on 2/26/2017.
 */
public class MirandaFileReadyState extends State {
    public MirandaFileReadyState (MirandaFile file) {
        super(file);
    }

    public MirandaFile getMirandaFile () {
        return (MirandaFile) getContainer();
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case FileChanged: {
                FileChangedMessage fileChangedMessage = (FileChangedMessage) message;
                nextState = processFileChangedMessage(fileChangedMessage);
                break;
            }

            case GarbageCollection: {
                GarbageCollectionMessage garbageCollectionMessage = (GarbageCollectionMessage) message;
                nextState = processGarbageCollectionMessage(garbageCollectionMessage);
                break;
            }

            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            default: {
                nextState = super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public void fireFileLoaded () {}

    public State processFileChangedMessage (FileChangedMessage fileChangedMessage) {
        try {
            getMirandaFile().load();
        } catch (IOException e) {
            Panic panic = new Panic("Exception loading file", e, Panic.Reasons.ExceptionLoadingFile);
            Miranda.panicMiranda(panic);
        }

        fireFileLoaded();

        return getMirandaFile().getCurrentState();
    }

    private State processGarbageCollectionMessage (GarbageCollectionMessage garbageCollectionMessage) {
        getMirandaFile().performGarbageCollection();
        return this;
    }

    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion(getMirandaFile().getName(), getMirandaFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getMirandaFile().getQueue(), this, nameVersion);
        send(getVersionMessage.getRequester(), versionMessage);

        return getMirandaFile().getCurrentState();
    }



}
