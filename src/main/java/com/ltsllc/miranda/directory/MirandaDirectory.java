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

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.StartupPanic;
import com.ltsllc.miranda.file.messages.FileChangedMessage;
import com.ltsllc.miranda.miranda.Miranda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 5/13/2017.
 */
abstract public class MirandaDirectory extends Consumer {
    abstract public boolean isInteresting (String name);


    private File directory;
    private List<File> files;

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public void setDirectoryName (String directoryName) {
        directory = new File(directoryName);
    }

    public String getDirectoryName () {
        return directory.getName();
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    public MirandaDirectory (String directoryName) {
        super(directoryName);
        this.directory = new File(directoryName);
        this.files = new ArrayList<File>();

        setDirectoryName(directoryName);

        FileChangedMessage fileChangedMessage = new FileChangedMessage(getQueue(), this, null);

        try {
            Miranda.fileWatcher.watch(getDirectory(), getQueue(), fileChangedMessage);
        } catch (IOException e) {
            StartupPanic startupPanic = new StartupPanic("Exception watching directory", StartupPanic.StartupReasons.ExceptionWatchingFile);
            Miranda.getInstance().panic(startupPanic);
        }

        DirectoryStartState directoryStartState = new DirectoryStartState(this);
        setCurrentState(directoryStartState);
    }

    public void start () {
        ScanTask scanTask = new ScanTask(this);
        scanTask.start();
    }

    public void sendScanCompleteMessage (List<File> files) {
        ScanCompleteMessage scanCompleteMessage = new ScanCompleteMessage(null, this, files);
        sendToMe(scanCompleteMessage);
    }

    public void sendExceptionDuringScanMessage (Throwable throwable) {
        ExceptionDuringScanMessage exceptionDuringScanMessage = new ExceptionDuringScanMessage(null, this, throwable);
        sendToMe(exceptionDuringScanMessage);
    }

}
