package com.ltsllc.miranda.file;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/10/2017.
 */
public class FileWatcherService extends Consumer {
    private static Logger logger = Logger.getLogger(FileWatcherService.class);
    private static FileWatcherService ourInstance;

    private long period;
    private Timer timer;
    private Map<String, Long> watchedFiles;
    private Map<String, List<FileWatcher>> watchers = new HashMap<String, List<FileWatcher>>();

    public FileWatcherService(int period) {
        super("file watcher");

        this.watchedFiles = new HashMap<String, Long>();
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

    public synchronized void checkFiles () {
        if (watchedFiles.size() > 0) {
            int i = 0;
            i++;
        }

        for (String canonicalName : watchedFiles.keySet())
        {
            File file = new File(canonicalName);
            long lastModified = new Long(file.lastModified());
            Long lastRecordChange = watchedFiles.get(canonicalName);

            if (lastModified != lastRecordChange.longValue())
                fireChanged(canonicalName);
        }
    }

    public void fireChanged (String canonicalName) {
        List<FileWatcher> list = watchers.get(canonicalName);
        for (FileWatcher fileWatcher : list) {
            fileWatcher.sendMessage();
        }
    }


    private String getCanonicalName (File file) {
        String name = null;

        try {
            name = file.getCanonicalPath();
        } catch (IOException e) {
            logger.fatal ("Exception while trying to get canonical name", e);
            System.exit(1);
        }

        return name;
    }


    public void watch (String filename, BlockingQueue<Message> queue, Message message) {
        File file = new File(filename);
        watch (file, queue, message);
    }

    public void watch(File file, BlockingQueue<Message> queue, Message message) {
        FileWatcher fileWatcher = new FileWatcher(queue, message);
        String canonicalName = getCanonicalName(file);
        List<FileWatcher> list = watchers.get(canonicalName);

        if (null == list) {
            list = new ArrayList<FileWatcher>();
            watchers.put(canonicalName, list);
        }

        list.add(fileWatcher);

        Long l = new Long(file.lastModified());
        watchedFiles.put(canonicalName, l);
    }


    /**
     * Note that this wont work if the client wants to be sent several messages
     * when a file is modified.  The method also does not check for the case
     * of no more watchers.
     *
     * @param file The file to watch.
     * @param queue The queue to send messages on.
     * @return true if this file was being watched.  False otherwise.
     */
    public synchronized boolean stopWatching (File file, BlockingQueue<Message> queue) {
        String canonicalName = getCanonicalName(file);
        List<FileWatcher> list = watchers.get (canonicalName);

        FileWatcher match = null;

        if (null != list)
        {
            for (FileWatcher fileWatcher : list) {
                if (fileWatcher.getQueue() == queue) {
                    match = fileWatcher;
                    break;
                }
            }

            if (null != match) {
                list.remove(match);
            }
        }

        return null != match;
    }


}
