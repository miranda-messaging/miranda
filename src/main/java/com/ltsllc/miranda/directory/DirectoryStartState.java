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

package com.ltsllc.miranda.directory;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.miranda.Miranda;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 5/13/2017.
 */
public class DirectoryStartState extends State {
    public MirandaDirectory getDirectory() {
        return (MirandaDirectory) getContainer();
    }

    public DirectoryStartState(MirandaDirectory directory) throws MirandaException {
        super(directory);
    }

    public State processMessage(Message message) throws MirandaException {
        State nextState = getDirectory().getCurrentState();

        switch (message.getSubject()) {
            case ScanCompleteMessage: {
                ScanCompleteMessage scanCompleteMessage = (ScanCompleteMessage) message;
                nextState = processScanCompleteMessage(scanCompleteMessage);
                break;
            }

            case ExceptionDuringScanMessage: {
                ExceptionDuringScanMessage exceptionDuringScanMessage = (ExceptionDuringScanMessage) message;
                nextState = processExceptionDuringScanMessage(exceptionDuringScanMessage);
                break;
            }

            default: {
                super.processMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processScanCompleteMessage(ScanCompleteMessage scanCompleteMessage) {
        List<File> files = new ArrayList<File>(scanCompleteMessage.getFiles());
        getDirectory().setFiles(files);

        return getDirectory().getCurrentState();
    }

    public State processExceptionDuringScanMessage(ExceptionDuringScanMessage exceptionDuringScanMessage) {
        StartupPanic startupPanic = new StartupPanic("Exception scanning diresctory", StartupPanic.StartupReasons.ExceptionScanning);
        Miranda.getInstance().panic(startupPanic);

        return getDirectory().getCurrentState();
    }
}
