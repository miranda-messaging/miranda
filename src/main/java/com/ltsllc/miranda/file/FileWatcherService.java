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

package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.clientinterface.MirandaException;
import com.ltsllc.miranda.file.messages.StopWatchingMessage;
import com.ltsllc.miranda.file.messages.WatchDirectoryMessage;
import com.ltsllc.miranda.file.messages.WatchFileMessage;
import com.ltsllc.miranda.file.states.FileWatcherReadyState;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * An Object that watches files and directories for changes.
 */
public class FileWatcherService extends Consumer {
    private static Logger logger = Logger.getLogger(FileWatcherService.class);
    private static FileWatcherService ourInstance;

    private long period;
    private Timer timer;
    private List<Watcher> watchers;

    public List<Watcher> getWatchers() {
        return watchers;
    }

    public long getPeriod() {
        return period;
    }

    public FileWatcherService(int period) throws MirandaException {
        super("file watcher");

        this.watchers = new ArrayList<Watcher>();
        this.period = (long) period;
        this.timer = new Timer("file system scanner", true);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkFiles();
            }
        };

        this.timer.scheduleAtFixedRate(timerTask, this.period, this.period);

        FileWatcherReadyState readyState = new FileWatcherReadyState(this);
        setCurrentState(readyState);
    }

    public synchronized void watchFile(File file, BlockingQueue<Message> listener) throws IOException {
        WatchFileMessage watchFileMessage = new WatchFileMessage(listener, null, file, listener);
        sendToMe(watchFileMessage);
    }

    public synchronized void watchDirectory(File directory, BlockingQueue<Message> listener) throws IOException {
        DirectoryWatcher directoryWatcher = new DirectoryWatcher(directory, listener);
        this.watchers.add(directoryWatcher);
    }

    public synchronized void stopWatching(File file, BlockingQueue<Message> listener) {
        List<Watcher> remove = new ArrayList<Watcher>();

        for (Watcher watcher : getWatchers()) {
            if (watcher.matches(file, listener))
                remove.add(watcher);
        }

        getWatchers().removeAll(remove);
    }


    public void check() {
        checkFiles();
    }


    public synchronized void checkFiles() {
        try {
            for (Watcher watcher : getWatchers()) {
                watcher.check();
            }
        } catch (IOException e) {
            Panic panic = new Panic("Excetion watching files", e, Panic.Reasons.ExceptionDuringScan);
            Miranda.panicMiranda(panic);
        }
    }

    public void sendWatchFileMessage(BlockingQueue<Message> senderQueue, Object sender, File file, BlockingQueue<Message> listener) {
        WatchFileMessage watchFileMessage = new WatchFileMessage(senderQueue, sender, file, listener);
        sendToMe(watchFileMessage);
    }

    public void sendWatchDirectoryMessage(BlockingQueue<Message> senderQueue, Object sender, File directory, BlockingQueue<Message> listener) {
        WatchDirectoryMessage watchDirectoryMessage = new WatchDirectoryMessage(senderQueue, sender, directory, listener);
        sendToMe(watchDirectoryMessage);
    }

    public void sendStopWatchingMessage(BlockingQueue<Message> senderQueue, Object sender, File file, BlockingQueue<Message> listener) {
        StopWatchingMessage stopWatchingMessage = new StopWatchingMessage(senderQueue, sender, file, listener);
        sendToMe(stopWatchingMessage);
    }

    public void watchDirectory(File file, BlockingQueue<Message> listener, Object sender) {
        WatchFileMessage watchFileMessage = new WatchFileMessage(listener, sender, file, listener);
        sendToMe(watchFileMessage);
    }

    public void addWatcher(File file, BlockingQueue<Message> listener) throws IOException {
        FileWatcher fileWatcher = null;

        if (file.isFile()) {
            fileWatcher = new SimpleFileWatcher(file, listener);
        } else if (file.isDirectory()) {
            fileWatcher = new DirectoryWatcher(file, listener);
        }

        getWatchers().add(fileWatcher);
    }
}

