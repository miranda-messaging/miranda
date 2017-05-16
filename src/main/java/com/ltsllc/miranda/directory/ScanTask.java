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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 5/13/2017.
 */
public class ScanTask implements Runnable {
    private MirandaDirectory mirandaDirectory;
    private Thread thread;

    public MirandaDirectory getMirandaDirectory() {
        return mirandaDirectory;
    }

    public void setMirandaDirectory(MirandaDirectory mirandaDirectory) {
        this.mirandaDirectory = mirandaDirectory;
    }

    public ScanTask (MirandaDirectory mirandaDirectory) {
        this.mirandaDirectory = mirandaDirectory;
    }

    public void start () {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void run () {
        List<File> files = new ArrayList<File>();
        try {
            walk(getMirandaDirectory().getDirectory(), files);
            getMirandaDirectory().sendScanCompleteMessage(files);
        } catch (IOException e) {
            getMirandaDirectory().sendExceptionDuringScanMessage(e);
        }
    }

    public void walk (File file, List<File> files) throws IOException {
        String[] listing = file.list();
        for (String name : listing) {
            File newFile = new File (file.getCanonicalFile(), name);
            if (getMirandaDirectory().isInteresting(name)) {
                files.add(newFile);
            }

            if (newFile.isDirectory()) {
                walk (newFile, files);
            }
        }
    }
}
